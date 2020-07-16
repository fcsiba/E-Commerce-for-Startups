using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Linq.Dynamic;
using ims_mat_pro.Models;
using System.Web.Security;

namespace ims_mat_pro.Controllers.Distributor
{
    public class DistributorController : Controller
    {
        linqDBContext dc = new linqDBContext();
        int retailType = 0;
        // GET: Distributor
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
                var query = (from us in db.tblDistributors
                             where us.id == id
                             select us).FirstOrDefault();
                if (query != null)
                {
                    ViewBag.data = query;
                }
            }

            return View();
        }


        public ActionResult AddDistributor(string txtName, string txtCity, string txtAddress, string txtNIC, string txtNTN, string txtMoblie, string txtContactNo, string txtReferredBy)
        {

            try
            {
               
                int assignedID = 0;
                string mob = txtMoblie.Replace("-", "");
                mob = mob.Replace(" ", String.Empty);
                using (linqDBContext db = new linqDBContext())
                {
                    var q = (from us in db.tblRetails
                             where us.name == "Distributors"
                             select us.id).FirstOrDefault();
                    if (q > 0)
                    {
                        retailType = q;
                    }

                    var chk = (from us in db.tblUsers
                               where us.userName == mob
                               select us).FirstOrDefault();
                    if (chk != null)
                    {
                        return Json(new JsonResult()
                        {
                            Data = 0
                        }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    { Random rn=new Random();
                        string pwd = rn.Next(1000,9999).ToString();

                        tblDistributor it = new tblDistributor();
                        it.Name = txtName;
                        it.City = txtCity;
                        it.Address = txtAddress;
                        it.NIC = txtNIC;
                        it.NTN = txtNTN;
                        it.Moblie = mob;
                        it.ContactNo = txtContactNo;
                        it.ReferredBy = txtReferredBy;
                        db.tblDistributors.Add(it);
                        db.SaveChanges();
                        assignedID = it.id;

                        tblUser u = new tblUser();
                        u.userName = mob;
                        u.password = pwd;
                        u.status = "Pending";
                        u.localID = assignedID;
                        u.type = retailType;
                       


                        tblMessage tm = new tblMessage();
                        tm.message = "New User Created with User Name " + mob + " And Password "+ pwd ;
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

        public ActionResult EditDistributor(int id, string Name, string City, string Address, string NIC, string NTN, string Moblie, string ContactNo, string ReferredBy)
        {
            try
            {
                string mob = Moblie.Replace("-", "");
                mob = mob.Replace(" ", String.Empty);
                using (linqDBContext db = new linqDBContext())
                {
                    var q = (from us in db.tblRetails
                             where us.name == "Distributors"
                             select us.id).FirstOrDefault();
                    if (q > 0)
                    {
                        retailType = q;
                    }

                    var query = (from us in db.tblDistributors
                                 where us.id == id
                                 select us).FirstOrDefault();
                    if (query != null)
                    {
                        query.Name = Name;
                        query.City = City;
                        query.Address = Address;
                        query.NIC = NIC;
                        query.NTN = NTN;
                        query.Moblie = mob;
                        query.ContactNo = ContactNo;
                        query.ReferredBy = ReferredBy;

                        db.SaveChanges();

                        var user = (from us in db.tblUsers
                                    where us.localID == id
                                    && us.type == retailType
                                    select us).FirstOrDefault();
                        if (user != null)
                        {
                            user.userName = mob;
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
                    var query = (from us in db.tblDistributors
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
                             where us.name == "Distributors"
                             select us.id).FirstOrDefault();
                    if (q > 0)
                    {
                        retailType = q;
                    }

                    var u = (from us in db.tblUsers
                             where us.localID == id
                             && us.type == retailType
                             select us).FirstOrDefault();
                    if (u != null)
                    {
                        db.tblUsers.Remove(u);
                        db.SaveChanges();
                    }
                    var data = db.tblDistributors.Single(x => x.id == id);
                    db.tblDistributors.Remove(data);
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
                    var v = (from a in dc.tblDistributors
                             select new { a.id, a.Name, a.City, a.Address, a.NIC, a.NTN, a.Moblie, a.ContactNo, a.ReferredBy });

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