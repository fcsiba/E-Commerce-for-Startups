using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Linq.Dynamic;
using ims_mat_pro.Models;
using System.Web.Security;

namespace ims_mat_pro.Controllers.Reseller
{
    public class ResellerController : Controller
    {
        linqDBContext dc = new linqDBContext();
        int retailType = 0;
        // GET: Reseller
        public ActionResult Index()

        {
            using (linqDBContext db = new linqDBContext())
            {
                int uType = Convert.ToInt16(Session["uType"]);
                int localID = Convert.ToInt16(Session["localID"]);

                if (uType != 2) //All resellers & supervisors
                {
                    //Supervisor
                    var category = (from us in db.tblResellers
                                    where us.isActive == true
                                    && us.isSupervisor == true
                                    //&& us.id == localID
                                    select us).ToList();

                    List<SelectListItem> item = new List<SelectListItem>();
                    if ((uType == 1) || (uType == 8))
                    {
                        item.Add(new SelectListItem
                        {
                            Text = "All",
                            Value = "0"
                        });
                    }
                    foreach (var itemm in category)
                    {
                        item.Add(new SelectListItem
                        {
                            Text = itemm.name,
                            Value = itemm.id.ToString()
                        });
                    }

                    ViewBag.super = item;

                    //Resellers
                    var category2 = (from us in db.tblResellers
                                    where us.isActive == true
                                    //&& us.supervisor == localID
                                    select us).ToList();

                    List<SelectListItem> item2 = new List<SelectListItem>();
                    if ((uType == 1) || (uType == 8))
                    {
                        item2.Add(new SelectListItem
                        {
                            Text = "All",
                            Value = "0"
                        });
                    }

                    foreach (var itemm in category2)
                    {
                        item2.Add(new SelectListItem
                        {
                            Text = itemm.name,
                            Value = itemm.id.ToString()
                        });
                    }

                    ViewBag.super2 = item2;

                }
                else //Relevant resellers & supervisors
                {

                    //Supervisors
                    var category = (from us in db.tblResellers
                                    where us.isActive == true
                                    && us.id == localID
                                    && us.isSupervisor == true
                                    select us).ToList();

                    List<SelectListItem> item = new List<SelectListItem>();
                    if ((uType == 1) || (uType == 8))
                    {
                        item.Add(new SelectListItem
                        {
                            Text = "All",
                            Value = "0"
                        });
                    }
                    foreach (var itemm in category)
                    {
                        item.Add(new SelectListItem
                        {
                            Text = itemm.name,
                            Value = itemm.id.ToString()
                        });
                    }

                    ViewBag.super = item;


                    //Resellers
                    var resellers = (from us in db.tblResellers
                                    where us.isActive == true
                                    && us.supervisor == localID                                   
                                    select us).ToList();

                    List<SelectListItem> itemR = new List<SelectListItem>();
                    if ((uType == 1) || (uType == 8))
                    {
                        itemR.Add(new SelectListItem
                        {
                            Text = "All",
                            Value = "0"
                        });
                    }
                    foreach (var itemm in resellers)
                    {
                        itemR.Add(new SelectListItem
                        {
                            Text = itemm.name,
                            Value = itemm.id.ToString()
                        });
                    }

                    ViewBag.super2 = itemR;

                }
               
            }


            return View();
        }

        public ActionResult Add(string name, string fName, int supervisor, string mob, string nic, string address)
        {
            try
            {
                int assignedID = 0;
                string mobile = mob.Replace("-", "");
                mobile = mobile.Replace(" ", String.Empty);
                using (linqDBContext db = new linqDBContext())
                {
                    var q = (from us in db.tblRetails
                             where us.name == "Reseller"
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
                    Random rn = new Random();
                    string pwd = rn.Next(1000, 9999).ToString();
                    tblReseller it = new tblReseller();
                    it.name = name;
                    it.fatherName = fName;
                    it.supervisor = supervisor;
                    it.Mob = mobile;
                    it.NIC = nic;
                    it.address = address;
                    it.isActive = true;
                    it.isSupervisor = false;
                    it.DOA = Convert.ToDateTime(DateTime.Now.ToShortDateString());
                    db.tblResellers.Add(it);
                    db.SaveChanges();
                    assignedID = it.id;

                    //Save for Login
                    tblUser u = new tblUser();
                    u.userName = mobile;
                    u.password = pwd;
                    u.status = "Approved";
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
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        public ActionResult Edit(int id, string name, string fName, int supervisor, string mob, string nic, string address)
        {
            try
            {
                string mobile = mob.Replace("-", "");
                mobile = mobile.Replace(" ", String.Empty);
                using (linqDBContext db = new linqDBContext())
                {
                    var q = (from us in db.tblRetails
                             where us.name == "Resellers"
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
                        query.supervisor = supervisor;
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

        public ActionResult SaveSettings(string share)
        {
            try
            {
                using (linqDBContext db = new linqDBContext())
                {
                    var query = (from us in db.tblShares
                                 where us.id == 1
                                 select us).FirstOrDefault();
                    if (query != null)
                    {
                        query.amount = Convert.ToInt32(share);
                        db.SaveChanges();                        
                    }
                    return Json(new JsonResult()
                    {
                        Data = "success"
                    }, JsonRequestBehavior.AllowGet);
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
                        },JsonRequestBehavior.AllowGet);
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
                             where us.name == "Resellers"
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
                            //Remove user
                            var user = (from us in db.tblUsers
                                        where us.localID == id
                                        && us.type == retailType
                                        select us).FirstOrDefault();
                            if (user != null)
                            {
                                db.tblUsers.Remove(user);
                                db.SaveChanges();
                            }

                            //Remove Reseller
                            db.tblResellers.Remove(data);
                            db.SaveChanges();

                            return Json(new JsonResult()
                            {
                                Data = "Success"
                            }, JsonRequestBehavior.AllowGet);
                        }
                        else
                        {
                            return Json(new { status = "error", Data = "Not found" }, JsonRequestBehavior.AllowGet);

                        }
                    }
                    else
                    {
                        return Json(new { status = "error", Data = "Reseller has data in previous orders" }, JsonRequestBehavior.AllowGet);
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
                    int localID = Convert.ToInt16(Session["localID"]);
                    int uType = Convert.ToInt16(Session["uType"]);
                    if ((uType == 2)) //Supervisor
                    {
                        var v = (from a in dc.tblResellers
                                 join b in dc.tblResellers
                                 on a.supervisor equals b.id
                                 join usrs in dc.tblUsers
                                 on a.id equals usrs.localID
                                 where a.isActive == true
                                 && a.isSupervisor == false
                                 && usrs.type == 3
                                 && a.supervisor == localID
                                 select new { a.id, a.name, a.fatherName, a.Mob, a.NIC, Supervisor = b.name, pw = usrs.password });

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
                                        m.Mob == searchValue || m.NIC == searchValue || m.Supervisor.Contains(searchValue));
                        }

                        recordsTotal = v.Count();
                        var data = v.Skip(skip).Take(pageSize).ToList();
                        var dt = data;
                        return Json(new { draw = draw, recordsFiltered = recordsTotal, recordsTotal = recordsTotal, data = data });
                    }
                    else //Admin
                    {
                        var v = (from a in dc.tblResellers
                                 join b in dc.tblResellers
                                 on a.supervisor equals b.id
                                 join usrs in dc.tblUsers
                                 on a.id equals usrs.localID
                                 where a.isActive == true
                                 && a.isSupervisor == false
                                 && usrs.type == 3
                                 select new { a.id, a.name, a.fatherName, a.Mob, a.NIC, Supervisor = b.name, pw = usrs.password });

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
                                        m.Mob == searchValue || m.NIC == searchValue || m.Supervisor.Contains(searchValue));
                        }

                        recordsTotal = v.Count();
                        var data = v.Skip(skip).Take(pageSize).ToList();
                        var dt = data;
                        return Json(new { draw = draw, recordsFiltered = recordsTotal, recordsTotal = recordsTotal, data = data });
                    }

                   

                }

            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }

        }
    }
}