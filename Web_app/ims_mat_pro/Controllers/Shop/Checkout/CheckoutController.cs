using ims_mat_pro.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Linq.Dynamic;
using ims_mat_pro.Controllers.Shop.Home;

namespace ims_mat_pro.Controllers.Checkout
{
    public class CheckoutController : Controller
    {       
        // GET: Checkout
        public ActionResult Index()
        {
            ViewBag.fName = Session["fName"];
            ViewBag.mob = Session["mob"];
            ViewBag.address = Session["address"];
            ViewBag.city = Session["city"];
            ViewBag.country = Session["country"];
            ViewBag.bill = Session["bill"];
            ViewBag.dc = Session["dc"];

            return View();
        }

        public ActionResult step02(string fName, string lName, string address, string mob, string city, string country, int bill, int dc)
        {
            string userID = "0";
            userID = Session["uID"].ToString();
            Session["fName"] = fName;           
            Session["address"] = address;
            Session["mob"] = mob;
            Session["city"] = city;
            Session["country"] = country;
            Session["bill"] = bill;
            Session["dc"] = dc;

            ViewBag.dc = dc;
            ViewBag.bill = bill;

            //Verification Page
            string tt = ShopController.GetTotalCount(userID);
            string[] arr = tt.Split('-');
            ViewBag.count = arr[0];
            ViewBag.total = arr[1];

            //Session["bill"] = arr[1];
            Session["bill-count"] = arr[0];

            using (linqDBContext db = new linqDBContext())
            {
                string content = "";
                string sectionsPath = Server.MapPath("~/data/templates/" + "cart-verification" + ".txt");
                content = System.IO.File.ReadAllText(sectionsPath);

                string cartContent = "";

                var cart = (from us in db.tblCarts
                            join it in db.tblItems
                            on us.itemID equals it.id
                            join cat in db.tblCategories
                            on it.subCategory equals cat.id
                            where us.userMob == userID
                            orderby it.id ascending
                            select new { us, it, cat }).ToList();
                if (cart != null)
                {
                    foreach (var item in cart)
                    {
                        cartContent += content;
                        cartContent = cartContent.Replace("#itemNameHere", item.it.itemName + " - " + item.us.size);
                        cartContent = cartContent.Replace("#itemCatHere", item.cat.name);
                        cartContent = cartContent.Replace("#itemQtyHere", item.us.itemQty.Value.ToString());
                        cartContent = cartContent.Replace("#itemPriceHere", "-");
                        cartContent = cartContent.Replace("#itemUnitPrice", "-");
                    }

                    ViewBag.data = cartContent;
                }

            }

            return View();
        }


        public ActionResult step3()
        {
            return View();
        }

        public ActionResult step04()
        {
            try
            {

                string fName = Session["fName"].ToString();               
                string address = Session["address"].ToString();
                string mob = Session["mob"].ToString();
                string city = Session["city"].ToString();
                string country = Session["country"].ToString();

                int userID = 0;
                userID = Convert.ToInt32(Session["uID"]);
                if (userID != 0)
                {
                    using (linqDBContext db = new linqDBContext())
                    {
                        string _mode = "Cash";                       

                        #region Orders

                        var cart = (from us in db.tblCarts
                                    where us.userMob == userID.ToString()
                                    select us).ToList();
                        if (cart != null)
                        {
                            int orderID = 0;

                            try
                            {
                                tblOrderSummary s = new tblOrderSummary();
                                s.userID = userID;
                                s.firstName = Session["fName"].ToString();
                                //s.lastName = Session["lName"].ToString();
                                s.shippingAddress = Session["address"].ToString();
                                s.mobile = Session["mob"].ToString();
                                s.city = Session["city"].ToString();
                                s.country = Session["country"].ToString();
                                s.totalAmount = Convert.ToDecimal(Session["bill"]) + Convert.ToDecimal(Session["dc"]);
                                s.netAmount = Convert.ToDecimal(Session["bill"]);
                                s.paid = 0;
                                s.discount = 0;
                                s.balance = s.totalAmount;
                                s.status = "Order Received";
                                s.deliveryOption = Program.delivery_option;
                                s.deliveryCharges = Convert.ToDecimal(Session["dc"]);
                                s.payMode = _mode;
                                s.note = "";
                                s.date = Convert.ToDateTime(DateTime.Now.ToShortDateString());
                                s.time = DateTime.Now;
                                db.tblOrderSummaries.Add(s);
                                db.SaveChanges();
                                orderID = s.id;
                            }
                            catch (Exception x)
                            {
                                return Json(new { status = "error", Data = "error in order summary: " +  x.Message }, JsonRequestBehavior.AllowGet);
                            }

                            try
                            {
                                foreach (var item in cart)
                                {
                                    tblItemsSold items_sold = new tblItemsSold();
                                    items_sold.invoiceNum = orderID;
                                    items_sold.itemID = item.itemID;
                                    items_sold.price = item.Price;
                                    items_sold.quantity = item.itemQty;
                                    items_sold.size = item.size;
                                    items_sold.userID = userID;
                                    items_sold.date = Convert.ToDateTime(DateTime.Now.ToShortDateString());
                                    db.tblItemsSolds.Add(items_sold);
                                    db.SaveChanges();

                                    //Update Inventory
                                    var query = (from us in db.tblItems
                                                 join inv in db.Inventory_Latest
                                                 on us.id equals inv.itemId
                                                 where inv.network == 3
                                                 && inv.localID == 0
                                                 && us.id == item.itemID
                                                 && inv.size == item.size
                                                 select inv).FirstOrDefault();
                                    if (query != null)
                                    {
                                        query.qty -= Convert.ToInt32(item.itemQty.Value);
                                        db.SaveChanges();
                                    }

                                    //Clear Cart
                                    db.tblCarts.Remove(item);
                                    db.SaveChanges();

                                }
                            }
                            catch (Exception x)
                            {
                                return Json(new { status = "error", Data = "error in items sold: " + x.Message }, JsonRequestBehavior.AllowGet);
                            }

                            try
                            {
                                ClearBillingSession();
                            }
                            catch (Exception x)
                            {
                                return Json(new { status = "error", Data = "error in session clearance: " + x.Message }, JsonRequestBehavior.AllowGet);
                            }

                          

                            return View();


                            //return Json(new { status = "success", Data = "Saved!" }, JsonRequestBehavior.AllowGet);
                        }
                        else
                        {
                            return Json(new { status = "error", Data = "Cart was empty!" }, JsonRequestBehavior.AllowGet);
                        }

                        #endregion

                    }
                }
                else
                {
                    return Json(new { status = "error", Data = "Please re-login." }, JsonRequestBehavior.AllowGet);
                }


            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }

            return View();
        }

        public ActionResult track()
        {
            return View();
        }


        public ActionResult Checkout(string mode, string name, string num, string expiry, string cvc)
        {
            try
            {

                string fName = Session["fName"].ToString();
                //string lName = Session["lName"].ToString();
                string address = Session["address"].ToString();
                string mob = Session["mob"].ToString();
                string city = Session["city"].ToString();
                string country = Session["country"].ToString();
                string error = "";

                int userID = 0;
                userID = Convert.ToInt32(Session["uID"]);
                if (userID != 0)
                {
                    using (linqDBContext db = new linqDBContext())
                    {
                        string _mode = "";
                        if (mode == "1")
                        {
                            _mode = "Bank";
                        }
                        else if (mode == "2")
                        {
                            #region CC

                            _mode = "CC";

                            var cc = (from us in db.tblCCs
                                      where us.userID == userID
                                      select us).FirstOrDefault();
                            if (cc != null)
                            {
                                cc.name = name;
                                cc.num = num;
                                cc.expiry = expiry;
                                cc.cvc = cvc;
                                db.SaveChanges();
                            }
                            else
                            {
                                tblCC c = new tblCC();
                                c.name = name;
                                c.num = num;
                                c.expiry = expiry;
                                c.cvc = cvc;
                                c.userID = userID;
                                db.tblCCs.Add(c);
                                db.SaveChanges();
                            }

                            #endregion
                        }
                        else if (mode == "3")
                        {
                            _mode = "Cash";
                        }

                        #region Orders

                        var cart = (from us in db.tblCarts
                                    where us.userMob == userID.ToString()
                                    select us).ToList();
                        if (cart != null)
                        {                          

                            var cartD = (from us in db.tblCarts
                                        where us.userMob == userID.ToString()
                                        select us.itemID).Distinct().ToList();

                            //Check inventory
                            foreach (var cItem in cartD)
                            {
                                var invData = (from us in db.tblSubInventories
                                               join item in db.tblItems
                                               on us.itemId equals item.id
                                               where us.itemId == cItem
                                               && us.issueRetailId == 1
                                               select new { us, item }).FirstOrDefault();

                                try
                                {
                                    var sCheck = (from us in db.tblCarts
                                                  where us.userMob == userID.ToString()
                                                  && us.itemID == cItem
                                                  && us.size == "S"
                                                  select us.itemQty).Sum();

                                    if (sCheck > invData.us.small)
                                    {
                                        error += "Inventory has only " + invData.us.small + " S left - " + invData.item.itemCode;
                                    }
                                    
                                }
                                catch (Exception x)
                                {
                                }

                                try
                                {
                                    var sCheck = (from us in db.tblCarts
                                                  where us.userMob == userID.ToString()
                                                  && us.itemID == cItem
                                                  && us.size == "M"
                                                  select us.itemQty).Sum();

                                    if (sCheck > invData.us.medium)
                                    {
                                        error += "Inventory has only " + invData.us.medium + " M left - " + invData.item.itemCode;
                                    }

                                }
                                catch (Exception x)
                                {
                                }

                                try
                                {
                                    var sCheck = (from us in db.tblCarts
                                                  where us.userMob == userID.ToString()
                                                  && us.itemID == cItem
                                                  && us.size == "L"
                                                  select us.itemQty).Sum();

                                    if (sCheck > invData.us.large)
                                    {
                                        error += "Inventory has only " + invData.us.large + " L left - " + invData.item.itemCode;
                                    }

                                }
                                catch (Exception x)
                                {
                                }

                                try
                                {
                                    var sCheck = (from us in db.tblCarts
                                                  where us.userMob == userID.ToString()
                                                  && us.itemID == cItem
                                                  && us.size == "L"
                                                  select us.itemQty).Sum();

                                    if (sCheck > invData.us.xLarge)
                                    {
                                        error += "Inventory has only " + invData.us.xLarge + " XL left - " + invData.item.itemCode;
                                    }

                                }
                                catch (Exception x)
                                {
                                }

                            }


                            if (error == "")
                            {
                                int orderID = 0;

                                tblOrderSummary s = new tblOrderSummary();
                                s.userID = userID;
                                s.firstName = Session["fName"].ToString();
                                //s.lastName = Session["lName"].ToString();
                                s.shippingAddress = Session["address"].ToString();
                                s.mobile = Session["mob"].ToString();
                                s.city = Session["city"].ToString();
                                s.country = Session["country"].ToString();
                                s.totalAmount = Convert.ToDecimal(Session["bill"]);
                                s.netAmount = s.totalAmount;
                                s.paid = 0;
                                s.discount = 0;
                                s.balance = s.totalAmount;
                                s.status = "Order Received";
                                s.deliveryOption = Program.delivery_option;
                                s.deliveryCharges = Convert.ToDecimal(Session["dc"]);
                                s.payMode = _mode;
                                s.note = "";
                                s.date = Convert.ToDateTime(DateTime.Now.ToShortDateString());
                                s.time = DateTime.Now;
                                db.tblOrderSummaries.Add(s);
                                db.SaveChanges();
                                orderID = s.id;

                                foreach (var item in cart)
                                {
                                    tblItemsSold items_sold = new tblItemsSold();
                                    items_sold.invoiceNum = orderID;
                                    items_sold.itemID = item.itemID;
                                    items_sold.price = item.Price;
                                    items_sold.quantity = item.itemQty;
                                    items_sold.size = items_sold.size;
                                    items_sold.userID = userID;
                                    items_sold.date = Convert.ToDateTime(DateTime.Now.ToShortDateString());
                                    db.tblItemsSolds.Add(items_sold);
                                    db.SaveChanges();

                                    //Update Inventory -- Pending
                                    var query = (from us in db.tblItems
                                                 join inv in db.tblSubInventories
                                                 on us.id equals inv.itemId
                                                 where inv.issueRetailId == 1
                                                 && us.id == item.itemID
                                                 select inv).FirstOrDefault();
                                    if (query != null)
                                    {
                                        if (item.size == "S")
                                        {
                                            query.small -= Convert.ToInt32(item.itemQty.Value);
                                        }
                                        else if (item.size == "M")
                                        {
                                            query.medium -= Convert.ToInt32(item.itemQty.Value);
                                        }
                                        else if (item.size == "L")
                                        {
                                            query.large -= Convert.ToInt32(item.itemQty.Value);
                                        }
                                        else if (item.size == "XL")
                                        {
                                            query.xLarge -= Convert.ToInt32(item.itemQty.Value);
                                        }
                                        db.SaveChanges();
                                    }

                                    //Clear Cart
                                    db.tblCarts.Remove(item);
                                    db.SaveChanges();

                                }

                                ClearBillingSession();
                            }
                            else
                            {
                                return Json(new { status = "error", Data = error }, JsonRequestBehavior.AllowGet);
                            }                           


                            //Send SMS
                            //var userQuery = (from us in db.tblUsers
                            //                 where us.id == userID
                            //                 && us.status == "Approved"
                            //                 select us).FirstOrDefault();
                            //if (userQuery != null)
                            //{
                            //    string msg = Sender.GetMessage("checkout");                               
                            //    msg = msg.Replace("{name}", userQuery.firstName);
                            //    Sender.SendSMS(userQuery.mobile, "", msg);
                            //}


                            return Json(new { status = "success", Data = "Saved!" }, JsonRequestBehavior.AllowGet);
                        }
                        else
                        {
                            return Json(new { status = "error", Data = "Cart was empty!" }, JsonRequestBehavior.AllowGet);
                        }

                        #endregion

                    }
                }
                else
                {
                    return Json(new { status = "error", Data = "Please re-login." }, JsonRequestBehavior.AllowGet);
                }
               

            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        public void ClearBillingSession()
        {
            Session["fName"] = null;
            //Session["lName"] = null;
            Session["address"] = null;
            Session["mob"] = null;
            Session["city"] = null;
            Session["country"] = null;
            Session["bill"] = null;
            Session["dc"] = null;
            Session["bill-count"] = null;
            Session["cart"] = Session["cart-empty"];

        }

        [HttpPost]
        public ActionResult LoadSummary()
        {
            try
            {
                var draw = Request.Form.GetValues("draw").FirstOrDefault();
                var start = Request.Form.GetValues("start").FirstOrDefault();
                var length = Request.Form.GetValues("length").FirstOrDefault();
                //Find Order Column
                var sortColumn = Request.Form.GetValues("columns[" + Request.Form.GetValues("order[0][column]").FirstOrDefault() + "][name]").FirstOrDefault();
                var sortColumnDir = Request.Form.GetValues("order[0][dir]").FirstOrDefault();

                sortColumn = "id";



                int pageSize = length != null ? Convert.ToInt32(length) : 0;
                int skip = start != null ? Convert.ToInt32(start) : 0;
                int recordsTotal = 0;

                using (linqDBContext dc = new linqDBContext())
                {
                    int uID = Convert.ToInt32(Session["uID"]);
                    //dc.Configuration.LazyLoadingEnabled = false; // if your table is relational, contain foreign key
                    var v = (from a in dc.tblOrderSummaries
                             where a.userID == uID
                             select new { a.id, customer = a.firstName, Bill = a.netAmount, DC = a.deliveryCharges, TBill = a.totalAmount, Paid = a.paid, Disc = a.discount, Bal = a.balance, date = a.date.Value.Day + "-" + a.date.Value.Month + "-" + a.date.Value.Year, DO = a.deliveryOption, a.status});

                    //SORT
                    if (!(string.IsNullOrEmpty(sortColumn) && string.IsNullOrEmpty(sortColumnDir)))
                    {
                        v = v.OrderBy(sortColumn + " " + sortColumnDir);
                    }

                    //Search   
                    var searchValue = Request.Form.GetValues("search[value]").FirstOrDefault();
                    if (!string.IsNullOrEmpty(searchValue))
                    {
                        v = v.Where(m => m.date.Contains(searchValue) ||
                                    m.status.ToString() == searchValue);
                    }

                    recordsTotal = v.Count();
                    var data = v.Skip(skip).Take(pageSize).ToList();
                    var dt = data;
                    return Json(new { draw = draw, recordsFiltered = recordsTotal, recordsTotal = recordsTotal, data = data });

                }






            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message });
            }

        }


        public ActionResult GetUserInfo()
        {
            try
            {
                int userID = 0;
                userID = Convert.ToInt32(Session["uID"]);
                if (userID > 0)
                {
                    using (linqDBContext db = new linqDBContext())
                    {
                        var query = (from us in db.tblUsers
                                     where us.id == userID
                                     select us).FirstOrDefault();
                        if (query != null)
                        {
                            return Json(new
                            {
                                status = "success",
                                Data = query
                            }, JsonRequestBehavior.AllowGet);
                        }
                        else
                        {
                            return Json(new { status = "error", Data = "No user found." }, JsonRequestBehavior.AllowGet);
                        }

                    }
                }
                else
                {
                    return Json(new { status = "error", Data = "Session expired! Please re-login." }, JsonRequestBehavior.AllowGet);
                }

            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

    }
}