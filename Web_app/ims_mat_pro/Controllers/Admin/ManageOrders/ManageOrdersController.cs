using ims_mat_pro.Models;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Linq.Dynamic;

namespace ims_mat_pro.Controllers
{
    public class ManageOrdersController : Controller
    {
        linqDBContext dc = new linqDBContext();
        // GET: ManageOrder
        public ActionResult Index()
        {
            using (linqDBContext db = new linqDBContext())
            {
                //Status
                var status = (from us in db.tblOrderSummaries
                              select us.status).Distinct().ToList();

                List<SelectListItem> item = new List<SelectListItem>();
                item.Add(new SelectListItem
                {
                    Text = "All",
                    Value = "0"
                });
                foreach (var itemm in status)
                {
                    item.Add(new SelectListItem
                    {
                        Text = itemm,
                        Value = itemm
                    });
                }

                ViewBag.status = item;

                //Delivered by
                var dBy = (from us in db.tblOrderSummaries
                              select us.deliveryOption).Distinct().ToList();

                List<SelectListItem> itemdBy = new List<SelectListItem>();
                itemdBy.Add(new SelectListItem
                {
                    Text = "All",
                    Value = "0"
                });
                foreach (var itemm in dBy)
                {
                    itemdBy.Add(new SelectListItem
                    {
                        Text = itemm,
                        Value = itemm
                    });
                }

                ViewBag.dBy = itemdBy;
            }
            

            return View();
        }

        public ActionResult Details()
        {
            try
            {
                int a = Convert.ToInt32(Session["invID"]);
                if (a == 0)
                {
                    return RedirectToAction("Index", "Login");
                }
                else
                {
                    using (linqDBContext db = new linqDBContext())
                    {
                        var data = (from us in db.tblOrderSummaries
                                    join cl in db.tblUsers
                                    on us.userID equals cl.id
                                    where us.id == a
                                    select new { us, cl }).FirstOrDefault();
                        if (data != null)
                        {
                            ViewBag.invNum = a;
                            ViewBag.name = data.us.firstName;
                            ViewBag.mob = data.us.mobile;
                            ViewBag.address = data.us.shippingAddress;
                        }
                    }
                }
            }
            catch (Exception x)
            {
                return RedirectToAction("Index", "Login");
            }           
            return View();
        }


        public ActionResult Sales()
        {

            return View();
        }


        public ActionResult UpdateStatus(int id, string status, string dBy, decimal paid, decimal dc)
        {
            try
            {
                using (linqDBContext db = new linqDBContext())
                {
                    var data = db.tblOrderSummaries.Where(m => m.id == id).FirstOrDefault();
                    if (data != null)
                    {                                             
                        data.status = status;
                        data.deliveryOption = dBy;
                        data.paid = Convert.ToDecimal(paid);
                        data.deliveryCharges = dc;
                        data.totalAmount = data.netAmount + dc;
                        data.balance = data.totalAmount - data.paid;
                        db.SaveChanges();


                        return Json(new { status = "success", Data = "Status Updated!" }, JsonRequestBehavior.AllowGet);


                    }
                    else
                    {
                        return Json(new { status = "error", Data = "Not found!" }, JsonRequestBehavior.AllowGet);

                    }
                }

            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }


        public ActionResult LoadDataNew()
        {
            try
            {
                var draw = Request.Form.GetValues("draw").FirstOrDefault();
                var start = Request.Form.GetValues("start").FirstOrDefault();
                var length = Request.Form.GetValues("length").FirstOrDefault();
                //Find Order Column
                var sortColumn = Request.Form.GetValues("columns[" + Request.Form.GetValues("order[0][column]").FirstOrDefault() + "][name]").FirstOrDefault();
                var sortColumnDir = Request.Form.GetValues("order[0][dir]").FirstOrDefault();


                int pageSize = length != null ? Convert.ToInt32(length) : 0;
                int skip = start != null ? Convert.ToInt32(start) : 0;
                int recordsTotal = 0;

                using (linqDBContext abc = new linqDBContext())
                {
                    //dc.Configuration.LazyLoadingEnabled = false; // if your table is relational, contain foreign key
                    var v = (from a in dc.tblOrderSummaries
                             join c in dc.tblUsers on a.userID equals c.id
                             join r in dc.tblResellers on c.localID equals r.id
                             where a.status == "Order Received"
                             orderby a.id descending
                             select new { a.id, Reseller = r.name, name = a.firstName, Bill = a.netAmount, DC = a.deliveryCharges, TBill = a.totalAmount, Paid = a.paid, Disc = a.discount, Bal = a.balance, a.payMode, a.status, date = a.date.Value.Day + "/" + a.date.Value.Month + "/" + a.date.Value.Year, dBy = a.deliveryOption });

                    sortColumnDir = "desc";
                    //SORT
                    if (!(string.IsNullOrEmpty(sortColumn) && string.IsNullOrEmpty(sortColumnDir)))
                    {
                        v = v.OrderBy(sortColumn + " " + sortColumnDir);
                    }

                    //Search   
                    var searchValue = Request.Form.GetValues("search[value]").FirstOrDefault();
                    if (!string.IsNullOrEmpty(searchValue))
                    {
                        v = v.Where(m => m.Reseller.Contains(searchValue) || m.name.Contains(searchValue));
                    }

                    recordsTotal = v.Count();
                    var data = v.Skip(skip).Take(pageSize).ToList();
                    var dt = data;
                    return Json(new { draw = draw, recordsFiltered = recordsTotal, recordsTotal = recordsTotal, data = data });

                }






            }
            catch (Exception x)
            {
                throw;
            }

        }

        public ActionResult LoadData()
        {
            try
            {
                var draw = Request.Form.GetValues("draw").FirstOrDefault();
                var start = Request.Form.GetValues("start").FirstOrDefault();
                var length = Request.Form.GetValues("length").FirstOrDefault();
                //Find Order Column
                var sortColumn = Request.Form.GetValues("columns[" + Request.Form.GetValues("order[0][column]").FirstOrDefault() + "][name]").FirstOrDefault();
                var sortColumnDir = Request.Form.GetValues("order[0][dir]").FirstOrDefault();


                int pageSize = length != null ? Convert.ToInt32(length) : 0;
                int skip = start != null ? Convert.ToInt32(start) : 0;
                int recordsTotal = 0;

                using (linqDBContext abc = new linqDBContext())
                {
                    //dc.Configuration.LazyLoadingEnabled = false; // if your table is relational, contain foreign key
                    var v = (from a in dc.tblOrderSummaries
                             join c in dc.tblUsers on a.userID equals c.id
                             join r in dc.tblResellers on c.localID equals r.id
                             orderby a.id descending
                             select new { a.id, Reseller = r.name, name = a.firstName, Bill = a.netAmount, DC = a.deliveryCharges, TBill = a.totalAmount, Paid = a.paid, Disc = a.discount, Bal = a.balance, a.payMode, a.status, date = a.date.Value.Day + "/" + a.date.Value.Month + "/" + a.date.Value.Year, dBy = a.deliveryOption });

                    sortColumnDir = "desc";
                    //SORT
                    if (!(string.IsNullOrEmpty(sortColumn) && string.IsNullOrEmpty(sortColumnDir)))
                    {
                        v = v.OrderBy(sortColumn + " " + sortColumnDir);
                    }

                    //Search   
                    var searchValue = Request.Form.GetValues("search[value]").FirstOrDefault();
                    if (!string.IsNullOrEmpty(searchValue))
                    {
                        v = v.Where(m => m.Reseller.Contains(searchValue) || m.name.Contains(searchValue));
                    }

                    recordsTotal = v.Count();
                    var data = v.Skip(skip).Take(pageSize).ToList();
                    var dt = data;
                    return Json(new { draw = draw, recordsFiltered = recordsTotal, recordsTotal = recordsTotal, data = data });

                }






            }
            catch (Exception x)
            {
                throw;
            }

        }

        [HttpPost]
        public ActionResult GetSelected(int id)
        {
            try
            {
                var q = (from us in dc.tblItemsSolds
                         join c in dc.tblItems on us.itemID equals c.id
                         where us.id == id
                         orderby us.id
                         select new { us.id, code = c.itemCode, c.itemName, us.quantity, us.price, qty = us.quantity, size = us.size }).FirstOrDefault();
                if (q != null)
                {
                    return Json(new { status = "success", data = q });
                }
                else
                {
                    return Json(new { status = "error", data = "not found." });
                }

                
            }
            catch (Exception x)
            {
                return Json(new { status = "error", data = x.Message });
            }         
         

        }

        [HttpPost]
        public ActionResult Return(int[] arr)
        {
            try
            {       
                using (linqDBContext db = new linqDBContext())
                {
                    int invoiceID = 0;
                    decimal priceSum = 0;
                    string code = "";
                    var data = (from us in db.tblItemsSolds
                                where arr.Contains(us.id)
                                select us).ToList();
                    if (data != null)
                    {
                        foreach (var item in data)
                        {
                            invoiceID = item.invoiceNum.Value;
                            priceSum += item.price.Value;
                            code += db.tblItems.Where(x => x.id == item.itemID).Select(y => y.itemCode).FirstOrDefault() + "   ";
                            
                            //update inv
                            var inv = (from us in db.Inventory_Latest
                                       where us.itemId == item.itemID
                                       && us.size == item.size
                                       && us.network == 3
                                       && us.localID == 0
                                       select us).FirstOrDefault();
                            if (inv != null)
                            {
                                int qty = Convert.ToInt32(item.quantity);
                                inv.qty += qty;
                            }

                        }

                        //update tblOrders summary
                        var summary = (from us in db.tblOrderSummaries
                                       where us.id == invoiceID
                                       select us).FirstOrDefault();
                        if (summary != null)
                        {
                            int cost = Convert.ToInt32(priceSum);
                            summary.netAmount -= cost;
                            summary.totalAmount -= cost;
                            summary.balance -= cost;
                            summary.note += Environment.NewLine + code + " (Returned)";
                        }

                        //Remove from tblItems Sold   
                        db.tblItemsSolds.RemoveRange(data);
                        db.SaveChanges();
                    }
                }

                return Json(new { status = "success", data = "done" });
            }
            catch (Exception x)
            {
                return Json(new { status = "error", data = x.Message });
            }


        }

        

        public ActionResult LoadDetails()
        {
            try
            {
                var draw = Request.Form.GetValues("draw").FirstOrDefault();
                var start = Request.Form.GetValues("start").FirstOrDefault();
                var length = Request.Form.GetValues("length").FirstOrDefault();
                //Find Order Column
                var sortColumn = Request.Form.GetValues("columns[" + Request.Form.GetValues("order[0][column]").FirstOrDefault() + "][name]").FirstOrDefault();
                var sortColumnDir = Request.Form.GetValues("order[0][dir]").FirstOrDefault();


                int pageSize = length != null ? Convert.ToInt32(length) : 0;
                int skip = start != null ? Convert.ToInt32(start) : 0;
                int recordsTotal = 0;

                using (linqDBContext abc = new linqDBContext())
                {
                    int invID = Convert.ToInt32(Session["invID"]);
                    //dc.Configuration.LazyLoadingEnabled = false; // if your table is relational, contain foreign key
                    var q = (from us in dc.tblItemsSolds
                             join c in dc.tblItems on us.itemID equals c.id
                             where us.invoiceNum == invID
                             orderby us.id
                             select new { us.id, code = c.itemCode,  c.itemName, us.quantity, us.price, qty = us.quantity, size = us.size });

                    //SORT
                    //if (!(string.IsNullOrEmpty(sortColumn) && string.IsNullOrEmpty(sortColumnDir)))
                    //{
                    //    q = q.OrderBy(sortColumn + " " + sortColumnDir);
                    //}
                   
                    //Search   
                    var searchValue = Request.Form.GetValues("search[value]").FirstOrDefault();
                    if (!string.IsNullOrEmpty(searchValue))
                    {
                        q = q.Where(m => m.itemName.Contains(searchValue));
                    }

                    recordsTotal = q.Count();
                    var data = q.Skip(skip).Take(pageSize).ToList();
                    var dt = data;
                    return Json(new { draw = draw, recordsFiltered = recordsTotal, recordsTotal = recordsTotal, data = data });

                }






            }
            catch (Exception x)
            {
                throw;
            }

        }


        public ActionResult LoadSales()
        {
            try
            {
                var draw = Request.Form.GetValues("draw").FirstOrDefault();
                var start = Request.Form.GetValues("start").FirstOrDefault();
                var length = Request.Form.GetValues("length").FirstOrDefault();
                //Find Order Column
                var sortColumn = Request.Form.GetValues("columns[" + Request.Form.GetValues("order[0][column]").FirstOrDefault() + "][name]").FirstOrDefault();
                var sortColumnDir = Request.Form.GetValues("order[0][dir]").FirstOrDefault();


                int pageSize = length != null ? Convert.ToInt32(length) : 0;
                int skip = start != null ? Convert.ToInt32(start) : 0;
                int recordsTotal = 0;

                using (linqDBContext abc = new linqDBContext())
                {

                    //dc.Configuration.LazyLoadingEnabled = false; // if your table is relational, contain foreign key
                    var q = (from us in dc.tblOrderSummaries
                             join c in dc.tblUsers on us.userID equals c.id
                             orderby us.id descending
                             select new { us.id, c.userName, us.status, us.date, us.totalAmount });

                    //SORT
                    if (!(string.IsNullOrEmpty(sortColumn) && string.IsNullOrEmpty(sortColumnDir)))
                    {
                        q = q.OrderBy(sortColumn + " " + sortColumnDir);
                    }

                    //Search   
                    var searchValue = Request.Form.GetValues("search[value]").FirstOrDefault();
                    if (!string.IsNullOrEmpty(searchValue))
                    {
                        q = q.Where(m => m.userName.Contains(searchValue));
                    }

                    recordsTotal = q.Count();
                    var data = q.Skip(skip).Take(pageSize).ToList();
                    var dt = data;
                    return Json(new { draw = draw, recordsFiltered = recordsTotal, recordsTotal = recordsTotal, data = data });

                }






            }
            catch (Exception x)
            {
                throw;
            }

        }
    }
}