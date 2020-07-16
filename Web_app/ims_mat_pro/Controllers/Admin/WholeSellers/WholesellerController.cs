using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Linq.Dynamic;
using ims_mat_pro.Models;
using System.Web.Security;

namespace ims_mat_pro.Controllers
{
    public class WholesellerController : Controller
    {
        linqDBContext dc = new linqDBContext();
        int retailType = 0;

        // GET: Wholeseller
        public ActionResult Index()
        {
            return View();
        }
    
        public ActionResult Add()
        {
            return View();
        }
        public ActionResult Edit(int id)
        {
            ViewBag.dID = id;
            using (linqDBContext db = new linqDBContext())
            {
                var query = (from us in db.tblWholesellers
                             where us.id == id
                             select us).FirstOrDefault();
                if (query != null)
                {
                    ViewBag.data = query;
                }
            }

            return View();
        }


        public ActionResult AddWholeseller(string txtName, string txtCity, string txtAddress,string txtType,  string txtNIC, string txtNTN, string txtMoblie, string txtContactNo, string txtReferredBy)
        {
            try
            {
                int assignedID = 0;
                string mobile = txtMoblie.Replace("-", "");
                mobile = mobile.Replace(" ", String.Empty);
                using (linqDBContext db = new linqDBContext())
                {
                    var q = (from us in db.tblRetails
                             where us.name == "Wholesellers"
                             select us.id).FirstOrDefault();
                    if (q > 0)
                    {
                        retailType = q;
                    }

                    var chk = (from us in db.tblUsers
                               where us.userName == mobile
                               select us).FirstOrDefault();
                    if (chk != null)
                    {
                        return Json(new JsonResult()
                        {
                            Data = 0
                        }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    {
                        Random rn = new Random();
                        string pwd = rn.Next(1000, 9999).ToString();
                        tblWholeseller it = new tblWholeseller();
                        it.Name = txtName;
                        it.City = txtCity;
                        it.Address = txtAddress;
                        it.Type = txtType;
                        it.NIC = txtNIC;
                        it.NTN = txtNTN;
                        it.Moblie = mobile;
                        it.ContactNo = txtContactNo;
                        it.ReferredBy = txtReferredBy;
                        db.tblWholesellers.Add(it);
                        db.SaveChanges();
                        assignedID = it.id;

                        tblUser u = new tblUser();
                        u.userName = mobile;
                        u.password = pwd;
                        u.status = "Pending";
                        u.localID = assignedID;
                        u.type = retailType;
                      

                        tblMessage tm = new tblMessage();
                        tm.message = "New User Created with User Name " + mobile + " And Password +" + pwd + " ";
                        tm.sent_time = DateTime.Now;
                        tm.isSent = false;

                        db.tblMessages.Add(tm);
                        db.tblUsers.Add(u);
                        db.SaveChanges();
                        return Json(new JsonResult()
                        {
                            Data = assignedID
                        }, JsonRequestBehavior.AllowGet);
                    }

                   

                }

               
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        public ActionResult EditWholeseller(int id, string Name, string City, string Address,string Type,string NIC, string NTN, string Moblie, string ContactNo, string ReferredBy)
        {
            try
            {
                string mobile = Moblie.Replace("-", "");
                mobile = mobile.Replace(" ", String.Empty);
                using (linqDBContext db = new linqDBContext())
                {
                    var q = (from us in db.tblRetails
                             where us.name == "Wholesellers"
                             select us.id).FirstOrDefault();
                    if (q > 0)
                    {
                        retailType = q;
                    }

                    var query = (from us in db.tblWholesellers
                                 where us.id == id
                                 select us).FirstOrDefault();
                    if (query != null)
                    {
                        query.Name = Name;
                        query.City = City;
                        query.Address = Address;
                        query.Type = Type;
                        query.NIC = NIC;
                        query.NTN = NTN;
                        query.Moblie = mobile;
                        query.ContactNo = ContactNo;
                        query.ReferredBy = ReferredBy;

                        db.SaveChanges();

                        var user = (from us in db.tblUsers
                                    where us.localID == id
                                    && us.type == retailType
                                    select us).FirstOrDefault();
                        if (user != null)
                        {
                            user.userName = mobile;
                            db.SaveChanges();
                        }
                        return Json(new JsonResult()
                        {
                            Data = "Success"
                        }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    {
                        return Json(new JsonResult()
                        {
                            Data = "Not found!"
                        }, JsonRequestBehavior.AllowGet);
                    }
                }


            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }


        public ActionResult Populate(int id)
        {
            try
            {
                using (linqDBContext db = new linqDBContext())
                {
                    var query = (from us in db.tblWholesellers
                                 where us.id == id
                                 select us).FirstOrDefault();
                    if (query != null)
                    {
                        return Json(new JsonResult()
                        {
                            Data = query
                        }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    {
                        return Json(new JsonResult()
                        {
                            Data = "Not found"
                        }, JsonRequestBehavior.AllowGet);
                    }

                }


            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        public ActionResult Delete(int id)
        {
            try
            {
                using (linqDBContext db = new linqDBContext())
                {
                    var q = (from us in db.tblRetails
                             where us.name == "Wholesellers"
                             select us.id).FirstOrDefault();
                    if (q > 0)
                    {
                        retailType = q;
                    }

                    var u = (from us in db.tblUsers
                             where us.type == retailType
                             && us.localID == id
                             select us).FirstOrDefault();
                    if (u != null)
                    {
                        db.tblUsers.Remove(u);
                        db.SaveChanges();
                    }

                    var data = db.tblWholesellers.Single(x => x.id == id);
                    db.tblWholesellers.Remove(data);
                    db.SaveChanges();
                    return Json(new { status = "success", Data = "done" }, JsonRequestBehavior.AllowGet);

                }


            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        [HttpPost]
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
                    var v = (from a in dc.tblWholesellers
                             select new { a.id, a.Name, a.City, a.Address,a.Type, a.NIC, a.NTN, a.Moblie, a.ContactNo, a.ReferredBy });

                    //SORT
                    if (!(string.IsNullOrEmpty(sortColumn) && string.IsNullOrEmpty(sortColumnDir)))
                    {
                        v = v.OrderBy(sortColumn + " " + sortColumnDir);
                    }

                    //Search   
                    var searchValue = Request.Form.GetValues("search[value]").FirstOrDefault();
                    if (!string.IsNullOrEmpty(searchValue))
                    {
                        v = v.Where(a => a.Name.Contains(searchValue));
                    }

                    recordsTotal = v.Count();
                    var data = v.Skip(skip).Take(pageSize).ToList();
                    var dt = data;
                    return Json(new { draw = draw, recordsFiltered = recordsTotal, recordsTotal = recordsTotal, data = data });

                }

            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }

        }
    }
}