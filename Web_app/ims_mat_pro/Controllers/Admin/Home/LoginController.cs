using ims_mat_pro.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace ims_mat_pro.Controllers.Home
{
    public class LoginController : Controller
    {
        // GET: Login
        public ActionResult Index()
        {
            return View();
        }

        public ActionResult CheckUser(string id)
        {
            try
            {
                using (linqDBContext db = new linqDBContext())
                {
                    var query = (from us in db.tblUsers
                                 where us.userName == id                                                   
                                 select us).FirstOrDefault();
                    if (query != null)
                    {
                        string formatted = "+92" + id.Substring(1, id.Length - 1);
                        if (formatted.Length == 13)
                        {
                            return Json(new { status = "success", Data = formatted }, JsonRequestBehavior.AllowGet);
                        }
                        else
                        {
                            return Json(new { status = "warning", Data = "Ask administrator to update your number!" }, JsonRequestBehavior.AllowGet);

                        }

                    }
                    else
                    {
                        return Json(new { status = "error", Data = "You're not allowed here!" }, JsonRequestBehavior.AllowGet);
                    }
                }
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }

        }


        public ActionResult VerifyUser(string num)
        {
            try
            {
                num = "0" + num.Substring(3, 10);
                using (linqDBContext db = new linqDBContext())
                {
                    var query = (from us in db.tblUsers
                                 where us.userName == num
                                 select us).FirstOrDefault();
                    if (query != null)
                    {
                        Session["uType"] = query.type;
                        Session["uID"] = query.id;
                        query.status = "Approved";
                        db.SaveChanges();
                        return Json(new { status = "success", Data = "Verified" }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    {
                        return Json(new { status = "error", Data = "Invalid verification code, please enter correct code." }, JsonRequestBehavior.AllowGet);
                    }

                }
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        public ActionResult Login(string username, string pw)
        {
            try
            {
                using (linqDBContext db = new linqDBContext())
                {
                    var chk = (from us in db.tblUsers
                               where us.userName == username
                               && us.password == pw
                               select us).FirstOrDefault();
                    if (chk != null)
                    {                        
                        if (chk.status == "Pending")
                        {
                            return Json(new JsonResult()
                            {
                                Data = "Verify"
                            }, JsonRequestBehavior.AllowGet);
                        }
                        else if(chk.status == "Approved")
                        {                           
                            Session["uType"] = chk.type;
                            Session["uID"] = chk.id;
                            Session["localID"] = chk.localID;
                            if (chk.type == 2 || chk.type == 3) //Supervirsor or Reseller
                            {
                                var query = (from us in db.tblResellers
                                             where us.id == chk.localID
                                             select us).FirstOrDefault();
                                if (query != null)
                                {
                                    Session["uName"] = query.name;
                                }
                            }
                            else
                            {
                                Session["uName"] = chk.userName;
                            }
                            
                            return Json(new JsonResult()
                            {
                                Data = chk.type
                            }, JsonRequestBehavior.AllowGet);                          
                            
                        }
                        else
                        {
                            return Json(new { status = "error", Data = "Invalid username password combination" }, JsonRequestBehavior.AllowGet);
                        }
                    }
                    else
                    {
                        return Json(new JsonResult()
                        {
                            Data = "Invalid"
                        }, JsonRequestBehavior.AllowGet);
                    }
                    
                }
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
           
        }

        public ActionResult LoginApp(string username, string pw)
        {
            try
            {
                using (linqDBContext db = new linqDBContext())
                {
                    var chk = (from us in db.tblUsers
                               where us.userName == username
                               && us.password == pw
                               select us).FirstOrDefault();
                    if (chk != null)
                    {
                        if (chk.status == "Pending")
                        {
                            return Json(new { status = "error", Data = "Invalid username password combination" }, JsonRequestBehavior.AllowGet);
                        }
                        else if (chk.status == "Approved")
                        {
                           
                            if (chk.type == 2 || chk.type == 3) //Supervirsor or Reseller
                            {
                                var query = (from us in db.tblResellers
                                             where us.id == chk.localID
                                             select us).FirstOrDefault();
                                if (query != null)
                                {
                                    return Json(new { status = "success", Data = new { uID = chk.id, localID = chk.localID, name = query.name } }, JsonRequestBehavior.AllowGet);
                                }
                            }
                            else
                            {
                                return Json(new { status = "error", Data = new { uID = chk.id, localID = chk.localID, name = "" } }, JsonRequestBehavior.AllowGet);
                            }

                            return Json(new { status = "error", Data = "Invalid username password combination" }, JsonRequestBehavior.AllowGet);


                        }
                        else
                        {
                            return Json(new { status = "error", Data = "Invalid username password combination" }, JsonRequestBehavior.AllowGet);
                        }
                    }
                    else
                    {
                        return Json(new { status = "error", Data = "Invalid username password combination" }, JsonRequestBehavior.AllowGet);
                    }

                }
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }

        }

        public ActionResult SaveSession(string key, string value)
        {
            Session[key] = value;
            return Json(new { status = "success", Data = "Saved." }, JsonRequestBehavior.AllowGet);
        }
    }
}