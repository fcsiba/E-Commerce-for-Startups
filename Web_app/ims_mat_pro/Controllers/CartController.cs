using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using ims_mat_pro.Models;

namespace ims_mat_pro.Controllers
{
    public class CartController : Controller
    {
        linqDBContext db = new linqDBContext();

        //// GET: ABOUT
        //public ActionResult Index(int id)
        //{
        //    ViewBag.cart = Session["cart"];
        //    if (ViewBag.cart == null)
        //    {
        //        //Latest Side Menu            
        //        string content = "";
        //        string sectionsPath = Server.MapPath("~/data/templates/" + "cartMini" + ".txt");
        //        content = System.IO.File.ReadAllText(sectionsPath);                
        //        Session["cart"] = content;
        //        Session["cart-empty"] = content;
        //        ViewBag.cart = Session["cart"];
        //    }

        //    ViewBag.home = "<a href='~/Home/Index?uID=" + Session["userMob"] + "'> Home</a>";

        //    GetSideList();
        //    GetLatestProduct();
        //    GetProducts(id);
        //    if (id == 1)
        //        ViewBag.type = "Fruits";
        //    else
        //        ViewBag.type = "Vegetables";

        //    Session["type"] = id;
        //    @Session["items-count"] = 0;
        //    @Session["items-total"] = 0;

        //    //Load cart from DB

            

        //    return View();
        //}

        //[HttpPost]
        //public ActionResult AddToCart(int id, decimal qty)
        //{
        //    try
        //    {
        //        string usrID = Session["uID"].ToString();                
               
        //        using (linqDBContext db = new linqDBContext())
        //        {
        //            var q = (from us in db.tblCarts
        //                     join item in db.tblItems on us.itemID equals item.id
        //                     where us.userMob == usrID
        //                     && us.itemID == id
        //                     select new { us, item }).FirstOrDefault();
        //            if (q != null)
        //            {
        //                if (q.us.itemQty != null)
        //                {
        //                    q.us.itemQty += qty;
        //                    q.us.Price = Convert.ToDecimal(q.us.itemQty.Value * q.item.netPrice.Value);
        //                    db.SaveChanges();
        //                }
        //                else
        //                {
        //                    q.us.itemQty = qty;
        //                    q.us.Price = Convert.ToDecimal(q.us.itemQty.Value * q.item.netPrice.Value);
        //                    db.SaveChanges();
        //                }
                       

        //                return Json(new JsonResult()
        //                {
        //                    Data = GetItem(id, usrID).ToList()
        //                }, JsonRequestBehavior.AllowGet);
        //            }
        //            else
        //            {
        //                var itemPrice = (from us in db.tblItems
        //                            where us.id == id
        //                            select us.netPrice.Value).FirstOrDefault();


        //                //Add
        //                tblCart c = new tblCart();
        //                c.userMob = usrID;
        //                c.itemQty = qty;
        //                c.itemID = id;
        //                c.Price = Convert.ToDecimal(qty * itemPrice);
        //                db.tblCarts.Add(c);
        //                db.SaveChanges();

                       

        //                return Json(new JsonResult()
        //                {
        //                    Data = GetItem(id, usrID).ToList()
        //                }, JsonRequestBehavior.AllowGet);
        //            }
        //        }

        //    }
        //    catch (Exception x)
        //    {
        //        return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
        //    }
        //}


        //[HttpPost]
        //public ActionResult RemoveFromCart(int id)
        //{
        //    try
        //    {
        //        string usrID = Session["uID"].ToString();

        //        using (linqDBContext db = new linqDBContext())
        //        {
        //            var q = (from us in db.tblCarts
        //                     join item in db.tblItems on us.itemID equals item.id
        //                     where us.userMob == usrID
        //                     && us.itemID == id
        //                     select us).FirstOrDefault();
        //            if (q != null)
        //            {
        //                db.tblCarts.Remove(q);
        //                db.SaveChanges();
                        
        //                return Json(new { status = "success", Data = "done" }, JsonRequestBehavior.AllowGet);
        //            }

        //            return Json(new { status = "not-found", Data = "not-found" }, JsonRequestBehavior.AllowGet);
        //        }

        //    }
        //    catch (Exception x)
        //    {
        //        return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
        //    }
        //}

        //[HttpPost]
        //public ActionResult GetTotal()
        //{
        //    try
        //    {
        //        string usrID = Session["uID"].ToString();
        //        string data = "";
        //        using (linqDBContext db = new linqDBContext())
        //        {
        //            try
        //            {
        //                var count = (from us in db.tblCarts
        //                             where us.userMob == usrID
        //                             select us.itemID.Value).Distinct().Count();
        //                if (count > 0)
        //                {
        //                    data = count + " ITEMS";
        //                }
        //                else
        //                {
        //                    data = "0 ITEM";
        //                }
        //            }
        //            catch (Exception x)
        //            {
        //                data = "0 ITEM";
        //            }

        //            try
        //            {
        //                //Total
        //                var total = (from us in db.tblCarts
        //                             where us.userMob == usrID
        //                             select us.Price.Value).Sum();
        //                if (total > 0)
        //                {
        //                    data += "-" + total;
        //                }
        //                else
        //                {
        //                    data += "-" + 0;
        //                }
        //            }
        //            catch (Exception)
        //            {
        //                data += "-" + 0;
        //            }
                   

        //            return Json(new { status = "success", Data = data }, JsonRequestBehavior.AllowGet);
        //        }
        //    }
        //    catch (Exception x)
        //    {
        //        return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
        //    }
        //}
                
        //public static string GetTotalCount(string usrID)
        //{
        //    try
        //    {                
        //        string data = "";
        //        using (linqDBContext db = new linqDBContext())
        //        {
        //            try
        //            {
        //                var count = (from us in db.tblCarts
        //                             where us.userMob == usrID
        //                             select us.itemID.Value).Distinct().Count();
        //                if (count > 0)
        //                {
        //                    data = count.ToString();
        //                }
        //                else
        //                {
        //                    data = "0";
        //                }
        //            }
        //            catch (Exception x)
        //            {
        //                data = "0";
        //            }

        //            try
        //            {
        //                //Total
        //                var total = (from us in db.tblCarts
        //                             where us.userMob == usrID
        //                             select us.Price.Value).Sum();
        //                if (total > 0)
        //                {
        //                    data += "-" + total;
        //                }
        //                else
        //                {
        //                    data += "-" + 0;
        //                }
        //            }
        //            catch (Exception)
        //            {
        //                data += "-" + 0;
        //            }


        //            return data;
        //        }
        //    }
        //    catch (Exception x)
        //    {
        //        return "0-0";
        //    }
        //}

        //[HttpPost]
        //public ActionResult ClearCart()
        //{
        //    try
        //    {
        //        string usrID = Session["uID"].ToString();              
        //        using (linqDBContext db = new linqDBContext())
        //        {
        //            var data = (from us in db.tblCarts
        //                        where us.userMob == usrID
        //                        select us).ToList();
        //            if (data != null)
        //            {
        //                foreach (var item in data)
        //                {
        //                    db.tblCarts.Remove(item);
        //                    db.SaveChanges();
        //                }
        //            }

        //            return Json(new { status = "success", Data = "done" }, JsonRequestBehavior.AllowGet);
        //        }
        //    }
        //    catch (Exception x)
        //    {
        //        return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
        //    }
        //}

        //public IEnumerable<Object> GetItem(int ids, string user)
        //{
        //    using (linqDBContext db = new linqDBContext())
        //    {
        //        var q = (from us in db.tblItems
        //                 join pic in db.tblItemPictures on us.id equals pic.itemID
        //                 join c in db.tblCarts on us.id equals c.itemID
        //                 where us.id == ids
        //                 && c.userMob == user
        //                 select new { us.id, us.itemName, pic.picLocation, c.itemQty,  us.netPrice,  c.userMob, pr = c.Price.Value }).ToList();
        //        if (q != null)
        //        {
        //            return q;
        //        }
        //        else
        //        {
        //            return q;
        //        }
        //    }

        //}

        //public void GetSideList()
        //{
        //    using (linqDBContext db = new linqDBContext())
        //    {
        //        var query = (from us in db.tblItems                             
        //                     select us).ToList();
        //        if (query != null)
        //        {
        //            string fruitList = "";
        //            string vegList = "";
        //            foreach (var item in query)
        //            {
        //                if (item.subCategory == 1)
        //                {
        //                    fruitList += "<li>" + item.itemName + "</li>";
        //                }
        //                else
        //                {
        //                    vegList += "<li>" + item.itemName + "</li>";
        //                }
        //            }

        //            ViewBag.fruit = fruitList;
        //            ViewBag.veg = vegList;
        //        }
        //    }
        //}

        //public void GetProducts(int id)
        //{
        //    using (linqDBContext db = new linqDBContext())
        //    {
        //        var query = (from us in db.tblItems
        //                     where us.subCategory == id
        //                     //orderby us.date.Value descending
        //                     select us).ToList();
        //        if (query != null)
        //        {
        //            //Latest Side Menu            
        //            string content = "";
        //            string sectionsPath = Server.MapPath("~/data/templates/" + "cartItem" + ".txt");
        //            content = System.IO.File.ReadAllText(sectionsPath);

        //            string str = "";

               
        //            foreach (var item in query)
        //            {
        //                var pic = (from pics in db.tblItemPictures
        //                           where pics.itemID == item.id
        //                           select pics.picLocation).FirstOrDefault();
        //                if (pic != null)
        //                {
        //                    str += content;
        //                    str = str.Replace("idHere", item.id + "");
        //                    str = str.Replace("captionHere", item.itemName);
        //                    str = str.Replace("priceHere", item.netPrice + "");
        //                    str = str.Replace("scaleHere", item.scale + "");
        //                    str = str.Replace("priceOHere", item.originalPrice + "");
        //                    str = str.Replace("srcHere", pic + "");
        //                }
        //            }

        //            ViewBag.Grid = str;
        //        }
        //    }
        //}

        //public void GetLatestProduct()
        //{
        //    using (linqDBContext db = new linqDBContext())
        //    {
        //        var query = (from us in db.tblItems
        //                     orderby us.date.Value descending
        //                     select us).ToList();
        //        if (query != null)
        //        {
        //            //Latest Side Menu            
        //            string content = "";
        //            string sectionsPath = Server.MapPath("~/data/templates/" + "latest" + ".txt");
        //            content = System.IO.File.ReadAllText(sectionsPath);

        //            string latestMenu = "";

        //            int i = 0;
        //            foreach (var item in query)
        //            {
        //                if (i == 3)
        //                    break;
        //                else
        //                {
        //                    var pic = (from pics in db.tblItemPictures                                       
        //                               where pics.itemID == item.id                                       
        //                               select pics.picLocation).FirstOrDefault();
        //                    if (pic != null)
        //                    {
        //                        latestMenu += content;
        //                        latestMenu = latestMenu.Replace("idHere", item.id + "");
        //                        latestMenu = latestMenu.Replace("captionHere", item.itemName);
        //                        latestMenu = latestMenu.Replace("priceHere", item.netPrice + "");
        //                        latestMenu = latestMenu.Replace("srcHere", pic + "");
        //                    }                           
        //                    i++;
        //                }
        //            }

        //            ViewBag.latest = latestMenu;
        //        }
        //    }
        //}

    }
       
}

public class cItems
{
    public int id  { get; set; }
   public  string itemName { get; set; }
   public string picLocation { get; set; }

    public decimal itemQty { get; set; }
    
    public int itemPrice { get; set; }

    public string userMob { get; set; }
}


