using ims_mat_pro.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Linq.Dynamic;

namespace ims_mat_pro.Controllers.Supervisor
{
    public class SupervisorController : Controller
    {
     
        linqDBContext dc = new linqDBContext();
        int retailType = 0;
        // GET: Supervisor
        public ActionResult Index()
        {
            return View();
        }

        public ActionResult Add(string name, string fName, string mob, string nic, string address)
        {
            try
            {
                retailType = 0;
                int assignedID = 0;
                string mobile = mob.Replace("-", "");
                mobile = mobile.Replace(" ", String.Empty);
                using (linqDBContext db = new linqDBContext())
                {
                    var q = (from us in db.tblRetails
                             where us.name == "Supervisors"
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

                    tblReseller it = new tblReseller();
                    it.name = name;
                    it.fatherName = fName;
                    //it.supervisor = it.id;
                    it.Mob = mobile;
                    it.NIC = nic;
                    it.address = address;
                    it.isActive = true;
                    it.isSupervisor = true;
                    it.DOA = Convert.ToDateTime(DateTime.Now.ToShortDateString());
                    db.tblResellers.Add(it);
                    db.SaveChanges();
                    assignedID = it.id;

                    Random rn = new Random();
                    string pwd = rn.Next(1000, 9999).ToString();

                    tblUser u = new tblUser();
                    u.userName = mobile;
                    u.status = "Approved";
                    u.localID = assignedID;
                    u.type = retailType;
                    u.password = pwd;
                    db.tblUsers.Add(u);
                    db.SaveChanges();

                    return Json(new JsonResult()
                    {
                        Data = assignedID
                    }, JsonRequestBehavior.AllowGet);

                }
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        public ActionResult Edit(int id, string name, string fName, string mob, string nic, string address)
        {
            try
            {
                retailType = 0;
                string mobile = mob.Replace("-", "");
                mobile = mobile.Replace(" ", String.Empty);
                using (linqDBContext db = new linqDBContext())
                {
                    var q = (from us in db.tblRetails
                             where us.name == "Supervisors"
                             select us.id).FirstOrDefault();
                    if (q > 0)
                    {
                        retailType = q;
                    }

                    var query = (from us in db.tblResellers
                                 where us.id == id
                                 select us).FirstOrDefault();
                    if (query != null)
                    {
                        query.name = name;
                        query.fatherName = fName;                      
                        query.Mob = mobile;
                        query.NIC = nic;
                        query.address = address;
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
                        else
                        {
                            Random rn = new Random();
                            string pwd = rn.Next(1000, 9999).ToString();

                            tblUser u = new tblUser();
                            u.userName = mobile;
                            u.status = "Approved";
                            u.localID = id;
                            u.type = retailType;
                            u.password = pwd;
                            db.tblUsers.Add(u);
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
                    var query = (from us in db.tblResellers
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
                             where us.name == "Supervisors"
                             select us.id).FirstOrDefault();
                    if (q > 0)
                    {
                        retailType = q;
                    }


                    var chk = (from us in db.tblOrderSummaries
                               where us.userID == id
                               select us).FirstOrDefault();
                    if (chk == null)
                    {
                        var data = (from us in db.tblResellers
                                    where us.id == id
                                    select us).FirstOrDefault();
                        if (data != null)
                        {
                            //Checl users working under
                            var usersWorking = (from us in db.tblResellers
                                                where us.supervisor == id
                                                select us).FirstOrDefault();
                            if (usersWorking == null)
                            {
                                //Remove user
                                var user = (from us in db.tblUsers
                                            where us.type == retailType
                                            && us.localID == id
                                            select us).FirstOrDefault();
                                if (user != null)
                                {
                                    db.tblUsers.Remove(user);
                                    db.SaveChanges();
                                }

                                //Remove Reseller
                                db.tblResellers.Remove(data);
                                db.SaveChanges();

                                return Json(new { status = "Success", Data = "Supervisor removed successfully!" }, JsonRequestBehavior.AllowGet);

                            }
                            else
                            {
                                return Json(new { status = "error", Data = "Resellers are working under this supervisor!" }, JsonRequestBehavior.AllowGet);
                            }

                            
                        }
                        else
                        {
                            return Json(new { status = "error", Data = "Not found" }, JsonRequestBehavior.AllowGet);

                        }
                    }
                    else
                    {
                        return Json(new { status = "error", Data = "Supervisor has data in previous orders" }, JsonRequestBehavior.AllowGet);
                    }
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
                    var v = (from a in dc.tblResellers
                             join usrs in dc.tblUsers
                             on a.id equals usrs.localID
                             where a.isActive == true
                             && a.isSupervisor == true
                             && usrs.type == 2
                             select new { a.id, a.name, a.fatherName, a.Mob, a.NIC, pw = usrs.password });                  

                    //SORT
                    if (!(string.IsNullOrEmpty(sortColumn) && string.IsNullOrEmpty(sortColumnDir)))
                    {
                        v = v.OrderBy(sortColumn + " " + sortColumnDir);
                    }

                    //Search   
                    var searchValue = Request.Form.GetValues("search[value]").FirstOrDefault();
                    if (!string.IsNullOrEmpty(searchValue))
                    {
                        v = v.Where(m => m.name.Contains(searchValue) ||
                                    m.Mob == searchValue || m.NIC == searchValue);
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