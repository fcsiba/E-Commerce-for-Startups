using System;
using System.Linq;
using System.Web.Mvc;
using System.Web.Security;
using System.Linq.Dynamic;
using ims_mat_pro.Models;
namespace ims_mat_pro.Controllers
{
    public class OutletsController : Controller
    {
        linqDBContext dc = new linqDBContext();
        int retailType = 0;
        // GET: Outlets
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
                var query = (from us in db.tblOutlets
                             where us.id == id
                             select us).FirstOrDefault();
                if (query != null)
                {
                    ViewBag.data = query;
                }
            }

            return View();
        }


        public ActionResult AddOutlet(string OutletName, string Phone, string Moblie, string NTN, string Location, string OwnerName, string NIC, string OwnerAddress, string ContactNo, string UserName, string UserMoblieNo)
        {
            try
            {
                int assignedID = 0;
                string mob = UserMoblieNo.Replace("-", "");
                mob = mob.Replace(" ", String.Empty);
                using (linqDBContext db = new linqDBContext())
                {
                    var q = (from us in db.tblRetails
                             where us.name == "Outlets"
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
                    {
                        Random rn = new Random();
                        string pwd = rn.Next(1000, 9999).ToString();
                        tblOutlet it = new tblOutlet();
                        it.OutletName = OutletName;
                        it.Phone = Phone;
                        it.Moblie = Moblie;
                        it.NTN = NTN;
                        it.Location = Location;
                        it.OwnerName = OwnerName;
                        it.NIC = NIC;
                        it.OwnerAddress = OwnerAddress;
                        it.ContactNo = ContactNo;
                        it.UserName = UserName;
                        it.UserMoblieNo = UserMoblieNo;

                        db.tblOutlets.Add(it);
                        db.SaveChanges();
                        assignedID = it.id;

                        tblUser u = new tblUser();
                        u.userName = mob;
                        u.password = pwd;
                        u.status = "Pending";
                        u.localID = assignedID;
                        u.type = retailType;
                       

                        tblMessage tm = new tblMessage();
                        tm.message = "New User Created with User Name " + mob + " And Password +" + pwd + " ";
                        tm.sent_time = DateTime.Now;
                        tm.isSent = false;
                        //new user and message will be saved

                        db.tblUsers.Add(u);
                        db.tblMessages.Add(tm);
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

        public ActionResult EditOutlet(int id, string OutletName, string Phone, string Moblie, string NTN, string Location, string OwnerName, string NIC, string OwnerAddress, string ContactNo, string UserName, string UserMoblieNo)
        {
            try
            {
                string mob = UserMoblieNo.Replace("-", "");
                mob = mob.Replace(" ", String.Empty);
                using (linqDBContext db = new linqDBContext())
                {
                    var q = (from us in db.tblRetails
                             where us.name == "Outlets"
                             select us.id).FirstOrDefault();
                    if (q > 0)
                    {
                        retailType = q;
                    }

                    var query = (from us in db.tblOutlets
                                 where us.id == id
                                 select us).FirstOrDefault();
                    if (query != null)
                    {
                        query.OutletName = OutletName;
                        query.Phone = Phone;
                        query.Moblie = Moblie;
                        query.NTN = NTN;
                        query.Location = Location;
                        query.OwnerName = OwnerName;
                        query.NIC = NIC;
                        query.OwnerAddress = OwnerAddress;
                        query.ContactNo = ContactNo;
                        query.UserName = UserName;
                        query.UserMoblieNo = UserMoblieNo;
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
                    var query = (from us in db.tblOutlets
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
                             where us.name == "Outlets"
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

                    var data = db.tblOutlets.Single(x => x.id == id);
                    db.tblOutlets.Remove(data);
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
                    var v = (from a in dc.tblOutlets
                             select new { a.id, a.OutletName, a.Phone, a.Moblie, a.NTN, a.Location, a.OwnerName, a.NIC, a.OwnerAddress, a.ContactNo, a.UserName, a.UserMoblieNo });

                    //SORT
                    if (!(string.IsNullOrEmpty(sortColumn) && string.IsNullOrEmpty(sortColumnDir)))
                    {
                        v = v.OrderBy(sortColumn + " " + sortColumnDir);
                    }

                    //Search   
                    var searchValue = Request.Form.GetValues("search[value]").FirstOrDefault();
                    if (!string.IsNullOrEmpty(searchValue))
                    {
                        v = v.Where(a => a.OutletName.Contains(searchValue));
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