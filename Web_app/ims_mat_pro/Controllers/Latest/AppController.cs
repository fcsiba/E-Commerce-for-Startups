using ims_mat_pro.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace ims_mat_pro.Controllers.Latest
{
    public class AppController : Controller
    {
        
        // GET: App
        public ActionResult Index()
        {
            return View();
        }

        public ActionResult GetProducts(int mainCat, int subCat)
        {
            using (linqDBContext db = new linqDBContext())
            {
                int mCat = mainCat;
                int catID = subCat;

                try
                {
                    var allItems = (from us in db.tblItems
                                    join inv in db.Inventory_Latest
                                    on us.id equals inv.itemId
                                    where inv.network == 3
                                    && inv.localID == 0
                                    && inv.qty > 0
                                    orderby us.date.Value descending
                                    select us).Distinct().ToList();
                    if (mCat > 0)
                    {
                        allItems = (from us in db.tblItems
                                    join inv in db.Inventory_Latest
                                    on us.id equals inv.itemId
                                    where inv.network == 3
                                    && inv.localID == 0
                                    && inv.qty > 0
                                    && us.mainCategory == mCat
                                    orderby us.date.Value descending
                                    select us).Distinct().ToList();
                    }
                    if (catID > 0)
                    {
                        allItems = (from us in db.tblItems
                                    join inv in db.Inventory_Latest
                                    on us.id equals inv.itemId
                                    where inv.network == 3
                                    && inv.localID == 0
                                    && inv.qty > 0
                                    && us.subCategory == catID
                                    orderby us.date.Value descending
                                    select us).Distinct().ToList();
                    }

                    var query = allItems.OrderByDescending(x => x.date.Value).ToList();
                    if (query != null)
                    {
                        List<ims_mat_pro.Models.Products> list = new List<ims_mat_pro.Models.Products>();
                        foreach (var item in query)
                        {
                            ims_mat_pro.Models.Products p = new Models.Products();
                            p.id = item.id;
                            p.itemCode = item.itemCode;
                            p.articleId = item.articleId;
                            p.itemName = item.itemName;
                            p.lotId = item.lotId;
                            p.mainCategory = item.mainCategory;
                            p.subCategory = item.subCategory;
                            p.originalPrice = item.originalPrice;
                            p.discount = item.discount;
                            p.description = item.description;
                            p.netPrice = item.netPrice;
                            p.fabric = item.fabric;
                            p.date = item.date;
                            p.coverImage = item.coverImage;
                            var sizes = db.Inventory_Latest.Where(x => x.itemId == item.id && x.network == 3 && x.localID == 0 && x.qty > 0).ToList();
                            p.sizes = new List<string>();
                            foreach (var itemSize in sizes)
                            {
                                p.sizes.Add(itemSize.size + "~" + itemSize.qty);
                            }
                            list.Add(p);

                        }

                        return Json(new { status = "success", Data = list }, JsonRequestBehavior.AllowGet);
                    }

                    return Json(new { status = "error", Data = "no data found." }, JsonRequestBehavior.AllowGet);
                }
                catch (Exception x)
                {
                    return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
                }
              
            }
        }

        public ActionResult Search(string str)
        {
            using (linqDBContext db = new linqDBContext())
            {
               
                try
                {
                    var allItems = (from us in db.tblItems
                                    join inv in db.Inventory_Latest
                                    on us.id equals inv.itemId
                                    where 
                                    inv.network == 3
                                    && inv.localID == 0
                                    && inv.qty > 0
                                    && us.description.Contains(str)
                                    ||
                                    inv.network == 3
                                    && inv.localID == 0
                                    && inv.qty > 0
                                    && us.itemCode.Contains(str)
                                    orderby us.date.Value descending
                                    select us).Distinct().ToList();                  
                    var query = allItems.OrderByDescending(x => x.date.Value).ToList();
                    if (query != null)
                    {
                        List<ims_mat_pro.Models.Products> list = new List<ims_mat_pro.Models.Products>();
                        foreach (var item in query)
                        {
                            ims_mat_pro.Models.Products p = new Models.Products();
                            p.id = item.id;
                            p.itemCode = item.itemCode;
                            p.articleId = item.articleId;
                            p.itemName = item.itemName;
                            p.lotId = item.lotId;
                            p.mainCategory = item.mainCategory;
                            p.subCategory = item.subCategory;
                            p.originalPrice = item.originalPrice;
                            p.discount = item.discount;
                            p.description = item.description;
                            p.netPrice = item.netPrice;
                            p.fabric = item.fabric;
                            p.date = item.date;
                            p.coverImage = item.coverImage;
                            var sizes = db.Inventory_Latest.Where(x => x.itemId == item.id && x.network == 3 && x.localID == 0 && x.qty > 0).ToList();
                            p.sizes = new List<string>();
                            foreach (var itemSize in sizes)
                            {
                                p.sizes.Add(itemSize.size + "~" + itemSize.qty);
                            }
                            list.Add(p);

                        }

                        return Json(new { status = "success", Data = list }, JsonRequestBehavior.AllowGet);
                    }

                    return Json(new { status = "error", Data = "no data found." }, JsonRequestBehavior.AllowGet);
                }
                catch (Exception x)
                {
                    return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
                }

            }
        }

        public ActionResult GetCart(string uID)
        {
            using (linqDBContext db = new linqDBContext())
            {             
                try
                {

                    var q = (from us in db.tblItems
                             join c in db.tblCarts on us.id equals c.itemID
                             where c.userMob == uID
                             select new { us.id, cartID = c.id, us.itemName, pic = us.coverImage, c.itemQty, us.netPrice, c.userMob, pr = c.Price.Value, size = c.size }).ToList();
                    if (q != null)
                    {
                        return Json(new { status = "success", Data = q }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    {
                        return Json(new { status = "error", Data = "no data found." }, JsonRequestBehavior.AllowGet);
                    }
                    
                    
                }
                catch (Exception x)
                {
                    return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
                }

            }
        }

        [HttpPost]
        public ActionResult AddToCart(int id, int uID, decimal qty, string size)
        {
            try
            {
                string usrID = uID.ToString();
                decimal quantity = qty;

                using (linqDBContext db = new linqDBContext())
                {

                    var cartQty = (from us in db.tblCarts
                                   where us.userMob == usrID
                                   && us.itemID == id
                                   && us.size == size
                                   select us).FirstOrDefault();
                    if (cartQty != null)
                    {
                        quantity += cartQty.itemQty.Value;
                    }

                    //Availibity
                    var chkAvailibity = (from us in db.Inventory_Latest
                                         where us.itemId == id
                                         && us.network == 3
                                         && us.localID == 0
                                         && us.size == size
                                         select us).FirstOrDefault();
                    if (quantity > chkAvailibity.qty)
                    {
                        if (chkAvailibity.qty == 0)
                        {
                            return Json(new { status = "error", Data = size + " size if out of stock." }, JsonRequestBehavior.AllowGet);
                        }
                        else
                        {
                            return Json(new { status = "error", Data = "Inventory has only " + chkAvailibity.qty + " " + size + " items left." }, JsonRequestBehavior.AllowGet);
                        }

                    }

                    var q = (from us in db.tblCarts
                             join item in db.tblItems on us.itemID equals item.id
                             where us.userMob == usrID
                             && us.itemID == id
                             && us.size == size
                             select new { us, item }).FirstOrDefault();
                    if (q != null)
                    {
                        if (qty < 0)
                        {
                            if ((q.us.itemQty == null) || (q.us.itemQty == 1))
                            {
                                return Json(new { status = "error", Data = "Cannot remove further." }, JsonRequestBehavior.AllowGet);
                            }
                        }
                        if (q.us.itemQty != null)
                        {
                            q.us.itemQty += qty;
                            q.us.Price = Convert.ToDecimal(q.us.itemQty.Value * q.item.netPrice.Value);
                            db.SaveChanges();
                        }
                        else
                        {
                            q.us.itemQty = qty;
                            q.us.Price = Convert.ToDecimal(q.us.itemQty.Value * q.item.netPrice.Value);
                            db.SaveChanges();
                        }

                        return Json(new { status = "success", Data = GetItem(q.us.id, usrID).ToList() }, JsonRequestBehavior.AllowGet);

                    }
                    else
                    {
                        var itemPrice = (from us in db.tblItems
                                         where us.id == id
                                         select us.netPrice.Value).FirstOrDefault();


                        //Add
                        tblCart c = new tblCart();
                        c.userMob = usrID;
                        c.itemQty = qty;
                        c.itemID = id;
                        c.size = size;
                        c.Price = Convert.ToDecimal(qty * itemPrice);
                        db.tblCarts.Add(c);
                        db.SaveChanges();



                        return Json(new { status = "success", Data = GetItem(c.id, usrID).ToList() }, JsonRequestBehavior.AllowGet);
                    }
                }

            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        public IEnumerable<Object> GetItem(int ids, string user)
        {
            using (linqDBContext db = new linqDBContext())
            {
                var q = (from us in db.tblItems
                         join c in db.tblCarts on us.id equals c.itemID
                         where c.id == ids
                         && c.userMob == user
                         select new { us.id, cartID = c.id, us.itemName, pic = us.coverImage, c.itemQty, us.netPrice, c.userMob, pr = c.Price.Value, size = c.size }).ToList();
                if (q != null)
                {
                    return q;
                }
                else
                {
                    return q;
                }
            }

        }

        [HttpPost]
        public ActionResult RemoveFromCart(int id, int uID)
        {
            try
            {
                string usrID = uID.ToString();

                using (linqDBContext db = new linqDBContext())
                {
                    var q = (from us in db.tblCarts
                             join item in db.tblItems on us.itemID equals item.id
                             where us.userMob == usrID
                             && us.id == id
                             select us).FirstOrDefault();
                    if (q != null)
                    {
                        db.tblCarts.Remove(q);
                        db.SaveChanges();

                        return Json(new { status = "success", Data = "done" }, JsonRequestBehavior.AllowGet);
                    }

                    return Json(new { status = "error", Data = "not-found" }, JsonRequestBehavior.AllowGet);
                }

            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        [HttpPost]
        public ActionResult Checkout(int uID, string name, string address, string mob, string city, string country, string bill, string dc)
        {
            using (linqDBContext db = new linqDBContext())
            {
                string userID = uID.ToString();
                var cart = (from us in db.tblCarts
                            where us.userMob == userID.ToString()
                            select us).ToList();
                if (cart != null)
                {
                    int orderID = 0;
                    bool invError = false;

                    try //Check inv count
                    {
                        foreach (var item in cart)
                        {
                            var query = (from us in db.tblItems
                                         join inv in db.Inventory_Latest
                                         on us.id equals inv.itemId
                                         where inv.network == 3
                                         && inv.localID == 0
                                         && us.id == item.itemID
                                         && inv.size == item.size
                                         select inv).FirstOrDefault();
                            if (item.itemQty > query.qty)
                            {
                                item.itemQty = 0;
                                item.Price = 0;                                
                                invError = true;
                            }
                        }
                        if (invError)
                        {
                            db.SaveChanges();
                            return Json(new { status = "error", Data = "inv" }, JsonRequestBehavior.AllowGet);
                        }
                    }
                    catch (Exception x)
                    {
                        return Json(new { status = "error", Data = "Error in checking inventory." }, JsonRequestBehavior.AllowGet);
                    }

                    try
                    {
                        tblOrderSummary s = new tblOrderSummary();
                        s.userID = uID;
                        s.firstName = name;
                        s.shippingAddress = address;
                        s.mobile = mob;
                        s.city = city;
                        s.country = country;
                        s.totalAmount = Convert.ToDecimal(bill) + Convert.ToDecimal(dc);
                        s.netAmount = Convert.ToDecimal(bill);
                        s.paid = 0;
                        s.discount = 0;
                        s.balance = s.totalAmount;
                        s.status = "Order Received";
                        s.deliveryOption = Program.delivery_option;
                        s.deliveryCharges = Convert.ToDecimal(dc);
                        s.payMode = "Cash";
                        s.note = "";
                        s.date = Convert.ToDateTime(DateTime.Now.ToShortDateString());
                        s.time = DateTime.Now;
                        db.tblOrderSummaries.Add(s);
                        db.SaveChanges();
                        orderID = s.id;
                    }
                    catch (Exception x)
                    {
                        return Json(new { status = "error", Data = "error in order summary: " + x.Message }, JsonRequestBehavior.AllowGet);
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
                            items_sold.userID = uID;
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


                        return Json(new { status = "success", Data = "Order saved." }, JsonRequestBehavior.AllowGet);
                    }
                    catch (Exception x)
                    {
                        return Json(new { status = "error", Data = "error in items sold: " + x.Message }, JsonRequestBehavior.AllowGet);
                    }


                }

                return Json(new { status = "error", Data = "Cart was empty" }, JsonRequestBehavior.AllowGet);

            }
        }


    }
}