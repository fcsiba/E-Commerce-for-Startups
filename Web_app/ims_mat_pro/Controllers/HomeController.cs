using ims_mat_pro.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace ims_mat_pro.Controllers
{
    public class HomeController : Controller
    {
        public ActionResult Index()
        {
            using (linqDBContext db = new linqDBContext())
            {
                var chk = (from us in db.tblItemPictures
                           where us.itemID == 0
                           select us).FirstOrDefault();
                if (chk != null)
                {
                    ViewBag.pic = chk.picLocation;
                }
                else
                {
                    ViewBag.pic = "main-bg.jpg";
                }

            }

            return View();
        }

        public ActionResult Logout()
        {
            Session.Clear();
            return RedirectToAction("Index", "Login");
        }


        public ActionResult Dashboard()
        {
            return View();
        }

        public ActionResult About()
        {
            ViewBag.Message = "Your application description page.";

            return View();
        }

        public ActionResult Contact()
        {
            ViewBag.Message = "Your contact page.";

            return View();
        }

        public ActionResult SaveLandingPic(string pic)
        {
            try
            {               
                using (linqDBContext db = new linqDBContext())
                {
                    var chk = (from us in db.tblItemPictures
                               where us.itemID == 0
                               select us).FirstOrDefault();
                    if (chk != null)
                    {
                        chk.picLocation = pic;
                        db.SaveChanges();
                    }
                    else
                    {
                        tblItemPicture p = new tblItemPicture();
                        p.itemID = 0;
                        p.picLocation = pic;
                        db.tblItemPictures.Add(p);
                        db.SaveChanges();
                    }

                }

                return Json(new { status = "success", Data = "Done" }, JsonRequestBehavior.AllowGet);
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }


        public ActionResult RemovePic()
        {
            try
            {
                using (linqDBContext db = new linqDBContext())
                {
                    var chk = (from us in db.tblItemPictures
                               where us.itemID == 0
                               select us).FirstOrDefault();
                    if (chk != null)
                    {
                        db.tblItemPictures.Remove(chk);
                        db.SaveChanges();
                    }
                  

                }

                return Json(new { status = "success", Data = "Done" }, JsonRequestBehavior.AllowGet);
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        [HttpPost]
        public ActionResult UpdateSession(string key, string value)
        {
            try
            {
                Session[key] = value;
                return Json(new { status = "success", Data = "done" }, JsonRequestBehavior.AllowGet);

            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }
    }
}