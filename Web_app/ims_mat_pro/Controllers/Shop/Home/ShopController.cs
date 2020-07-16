using CrystalDecisions.CrystalReports.Engine;
using CrystalDecisions.Shared;
using ims_cloud.Controllers;
using ims_mat_pro.Models;
using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.IO;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace ims_mat_pro.Controllers.Shop.Home
{
    public class ShopController : Controller
    {
        int productsOnPage = 20;
        List<tblItem> allItems = new List<tblItem>();

        // GET: Shop
        public ActionResult Index()
        {
            using (linqDBContext db = new linqDBContext())
            {
                try
                {
                    int catID = 0;
                    try
                    {
                        catID = Convert.ToInt32(Session["catID"]);
                    }
                    catch (Exception x)
                    {
                        Session["catID"] = 0;
                    }

                    var dc = (from us in db.tblShares
                              where us.type == "2"
                              select us.amount.Value).FirstOrDefault();
                    if (dc > 0)
                    {
                        Program.delivery_charges = dc;
                    }

                    var proCount = (from us in db.tblShares
                                    where us.type == "3"
                                    select us.amount.Value).FirstOrDefault();
                    if (proCount > 0)
                    {
                        productsOnPage = Convert.ToInt32(proCount);
                    }

                    if (Session["mCat"] == null)
                    {
                        Session["mCat"] = 1;
                    }

                    int mCat = Convert.ToInt32(Session["mCat"]);
                    var mCatTitle = db.tblMainCategories.Where(x => x.id == mCat).Select(y => y.name).FirstOrDefault() + "'s Product";
                    Session["catTitle"] = mCatTitle;

                    var cat = (from us in db.tblItems
                               join inv in db.Inventory_Latest
                               on us.id equals inv.itemId
                               where inv.network == 3
                               && inv.localID == 0
                               && inv.qty > 0
                               && us.mainCategory == mCat
                               select us.subCategory).Distinct().ToList();
                    if (cat != null)
                    {
                        var catQuery = (from us in db.tblCategories
                                        where cat.Contains(us.id)
                                        select us).ToList();
                        ViewBag.cat = catQuery;
                    }

                    var pages = (from us in db.tblItems
                                 join inv in db.Inventory_Latest
                                 on us.id equals inv.itemId
                                 where inv.network == 3
                                 && inv.localID == 0
                                 && inv.qty > 0
                                 && us.mainCategory == mCat
                                 select us).Distinct().Count();
                    if (catID > 0)
                    {
                        pages = (from us in db.tblItems
                                 join inv in db.Inventory_Latest
                                 on us.id equals inv.itemId
                                 where inv.network == 3
                                 && inv.localID == 0
                                 && inv.qty > 0
                                 && us.mainCategory == mCat
                                 && us.subCategory == catID
                                 select us).Distinct().Count();
                    }
                    if (pages > 0)
                    {
                        double roundedUp = Math.Ceiling(pages / Convert.ToDouble(productsOnPage));
                        int totalPages = Convert.ToInt32(roundedUp);
                        ViewBag.pages = totalPages;

                    }


                    //Old
                    //var pages = (from us in db.tblItems
                    //             join inv in db.tblSubInventories
                    //             on us.id equals inv.itemId
                    //             where inv.issueRetailId == 1 
                    //             select inv.itemId).Distinct().Count();
                    //if (pages > 0)
                    //{
                    //    double roundedUp = Math.Ceiling(pages / Convert.ToDouble(productsOnPage));                       
                    //    int totalPages = Convert.ToInt32(roundedUp);
                    //    ViewBag.pages = totalPages;

                    //}

                    GetProducts();
                }
                catch (Exception x)
                {}

                try
                {
                    int superID = Convert.ToInt32(Session["localID"]);
                    var category = (from us in db.tblResellers
                                    where us.isActive == true
                                    && us.supervisor == superID
                                    select us).ToList();

                    List<SelectListItem> item = new List<SelectListItem>();
                    item.Add(new SelectListItem
                    {
                        Text = "All",
                        Value = "0"
                    });
                    foreach (var itemm in category)
                    {
                        item.Add(new SelectListItem
                        {
                            Text = itemm.name,
                            Value = itemm.id.ToString()
                        });
                    }

                    ViewBag.super = item;
                }
                catch (Exception x)
                {}
            }
            return View();
        }

        public void GetProducts()
        {
            using (linqDBContext db = new linqDBContext())
            {
                //Old
                //allItems = (from us in db.tblItems
                //             join inv in db.tblSubInventories
                //             on us.id equals inv.itemId
                //             where inv.issueRetailId == 1                           
                //             select us).ToList();

                //var allItems = (from us in db.tblItems
                //            join inv in db.Inventory_Latest
                //            on us.id equals inv.itemId
                //            where inv.network == 3
                //            && inv.localID == 0
                //            && inv.qty > 0
                //            select us).Distinct().ToList();

                int mCat = Convert.ToInt32(Session["mCat"]);
                int catID = Convert.ToInt32(Session["catID"]);

                allItems = (from us in db.tblItems
                            join inv in db.Inventory_Latest
                            on us.id equals inv.itemId
                            where inv.network == 3
                            && inv.localID == 0         
                            && inv.qty > 0   
                            && us.mainCategory == mCat   
                            orderby us.date.Value descending                                     
                            select us).Distinct().ToList();
                if (catID > 0)
                {
                    allItems = (from us in db.tblItems
                                join inv in db.Inventory_Latest
                                on us.id equals inv.itemId
                                where inv.network == 3
                                && inv.localID == 0
                                && inv.qty > 0
                                && us.mainCategory == mCat
                                && us.subCategory == catID
                                orderby us.date.Value descending
                                select us).Distinct().ToList();
                }

                var query = allItems.OrderByDescending(x=> x.date.Value).Skip(0).Take(productsOnPage).ToList();
                if (query != null)
                {
                    //Latest Side Menu            
                    string content = "";
                    string sectionsPath = Server.MapPath("~/data/templates/" + "shop" + ".txt");
                    content = System.IO.File.ReadAllText(sectionsPath);

                    string str = "";


                    foreach (var item in query)
                    {
                        var sizes = db.Inventory_Latest.Where(x => x.itemId == item.id && x.network == 3 && x.localID == 0 && x.qty > 0).ToList();
                        var cat = db.tblMainCategories.Where(x => x.id == item.mainCategory.Value).FirstOrDefault();
                        if (cat != null)
                        {
                            str += content;
                            str = str.Replace("idHere", item.id + "");
                            str = str.Replace("captionHere", item.itemName);
                            str = str.Replace("codeHere", item.itemCode);
                            str = str.Replace("priceHere", Convert.ToInt32(item.netPrice) + "");
                            str = str.Replace("scaleHere", item.scale + "");
                            str = str.Replace("priceOHere", item.originalPrice + "");
                            str = str.Replace("coverSource", item.coverImage + "");
                            str = str.Replace("catTagHere", cat.id + "");
                            string dropdown = "<option value='#size'>#val</option>";
                            string strDropdown = "";
                            foreach (var itemSize in sizes)
                            {
                                strDropdown += dropdown.Replace("#size", itemSize.size).Replace("#val", itemSize.size + " - " + itemSize.qty + " left");                
                            }
                            str = str.Replace("#sizeItem", strDropdown);

                            //var invQuery = (from us in db.tblSubInventories
                            //                where us.itemId == item.id
                            //                && us.issueRetailId == 1
                            //                select us).FirstOrDefault();
                            //if (invQuery != null)
                            //{
                            //    str = str.Replace("invHere", "S (" + invQuery.small.Value.ToString() + ") M (" + invQuery.medium.Value.ToString() + ") L (" + invQuery.large.Value.ToString() + ") XL (" + invQuery.xLarge.Value.ToString() + ")");
                            //}


                            if (item.tag == "")
                            {
                                str = str.Replace("label-new", "");
                            }
                            else
                            {
                                str = str.Replace("itemTagHere", item.tag + "");
                            }
                        }

                    }

                    ViewBag.Grid = str;
                }
            }
        }

        [HttpPost]
        public ActionResult GetMoreProducts(int page)
        {
            try
            {
                page = page - 1;
                int skip = page * productsOnPage;

                using (linqDBContext db = new linqDBContext())
                {
                    int mCat = Convert.ToInt32(Session["mCat"]);
                    int catID = Convert.ToInt32(Session["catID"]);

                    allItems = (from us in db.tblItems
                                join inv in db.Inventory_Latest
                                on us.id equals inv.itemId
                                where inv.network == 3
                                && inv.localID == 0
                                && inv.qty > 0
                                && us.mainCategory == mCat
                                orderby us.date.Value descending
                                select us).Distinct().ToList();
                    if (catID > 0)
                    {
                        allItems = (from us in db.tblItems
                                    join inv in db.Inventory_Latest
                                    on us.id equals inv.itemId
                                    where inv.network == 3
                                    && inv.localID == 0
                                    && inv.qty > 0
                                    && us.mainCategory == mCat
                                    && us.subCategory == catID
                                    orderby us.date.Value descending
                                    select us).Distinct().ToList();
                    }

                    var query = allItems.OrderByDescending(x => x.date.Value).Skip(skip).Take(productsOnPage).ToList();
                    if (query != null)
                    {
                        //Latest Side Menu            
                        string content = "";
                        string sectionsPath = Server.MapPath("~/data/templates/" + "shop" + ".txt");
                        content = System.IO.File.ReadAllText(sectionsPath);

                        string str = "";


                        foreach (var item in query)
                        {
                            var sizes = db.Inventory_Latest.Where(x => x.itemId == item.id && x.network == 3 && x.localID == 0 && x.qty > 0).ToList();
                            var cat = db.tblMainCategories.Where(x => x.id == item.mainCategory.Value).FirstOrDefault();
                            if (cat != null)
                            {
                                str += content;
                                str = str.Replace("idHere", item.id + "");
                                str = str.Replace("captionHere", item.itemName);
                                str = str.Replace("codeHere", item.itemCode);
                                str = str.Replace("priceHere", Convert.ToInt32(item.netPrice) + "");
                                str = str.Replace("scaleHere", item.scale + "");
                                str = str.Replace("priceOHere", item.originalPrice + "");
                                str = str.Replace("coverSource", item.coverImage + "");
                                str = str.Replace("catTagHere", cat.id + "");
                                str = str.Replace("descHere", item.description + "");
                                if (item.tag == "")
                                {
                                    str = str.Replace("label-new", "");
                                }
                                else
                                {
                                    str = str.Replace("itemTagHere", item.tag + "");
                                }                                

                                string dropdown = "<option value='#size'>#val</option>";
                                string strDropdown = "";
                                foreach (var itemSize in sizes)
                                {
                                    strDropdown += dropdown.Replace("#size", itemSize.size).Replace("#val", itemSize.size + " - " + itemSize.qty + " left");
                                }
                                str = str.Replace("#sizeItem", strDropdown);

                                //var invQuery = (from us in db.tblSubInventories
                                //                where us.itemId == item.id
                                //                && us.issueRetailId == 1
                                //                select us).FirstOrDefault();
                                //if (invQuery != null)
                                //{
                                //    str = str.Replace("invHere", "S (" + invQuery.small.Value.ToString() + ") M (" + invQuery.medium.Value.ToString() + ") L (" + invQuery.large.Value.ToString() + ") XL (" + invQuery.xLarge.Value.ToString() + ")");
                                //}
                            }

                        }

                        return Json(new { status = "success", Data = str }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    {
                        return Json(new { status = "error", Data = "No data found." }, JsonRequestBehavior.AllowGet);
                    }
                }
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        public ActionResult SearchProduct(string name)
        {
            try
            {             

                using (linqDBContext db = new linqDBContext())
                {

                    var query = (from us in db.tblItems
                                 join inv in db.Inventory_Latest
                                 on us.id equals inv.itemId
                                 where inv.network == 3
                                 && inv.localID == 0
                                  && inv.qty > 0
                                 orderby us.date.Value descending
                                 select us).Distinct().ToList();
                    if (name != "")
                    {
                        query = (from us in db.tblItems
                                 join inv in db.Inventory_Latest
                                 on us.id equals inv.itemId
                                 where inv.network == 3
                                 && inv.localID == 0
                                 && us.itemCode.Contains(name) 
                                 && inv.qty > 0 
                                 ||
                                 inv.network == 3
                                 && inv.localID == 0
                                 && us.description.Contains(name)
                                 && inv.qty > 0
                                 orderby us.date.Value descending
                                 select us).Distinct().ToList();
                    }
                   
                    if (query != null)
                    {
                        //Latest Side Menu            
                        string content = "";
                        string sectionsPath = Server.MapPath("~/data/templates/" + "shop" + ".txt");
                        content = System.IO.File.ReadAllText(sectionsPath);

                        string str = "";


                        foreach (var item in query)
                        {
                            var sizes = db.Inventory_Latest.Where(x => x.itemId == item.id && x.network == 3 && x.localID == 0 && x.qty > 0).ToList();

                            var cat = db.tblMainCategories.Where(x => x.id == item.mainCategory.Value).FirstOrDefault();
                            if (cat != null)
                            {
                                str += content;
                                str = str.Replace("idHere", item.id + "");
                                str = str.Replace("captionHere", item.itemName);
                                str = str.Replace("codeHere", item.itemCode);
                                str = str.Replace("priceHere", Convert.ToInt32(item.netPrice) + "");
                                str = str.Replace("scaleHere", item.scale + "");
                                str = str.Replace("priceOHere", item.originalPrice + "");
                                str = str.Replace("coverSource", item.coverImage + "");
                                str = str.Replace("catTagHere", cat.id + "");
                                str = str.Replace("descHere", item.description + "");
                                if (item.tag == "")
                                {
                                    str = str.Replace("label-new", "");
                                }
                                else
                                {
                                    str = str.Replace("itemTagHere", item.tag + "");
                                }


                                string dropdown = "<option value='#size'>#val</option>";
                                string strDropdown = "";
                                foreach (var itemSize in sizes)
                                {
                                    strDropdown += dropdown.Replace("#size", itemSize.size).Replace("#val", itemSize.size + " - " + itemSize.qty + " left");
                                }
                                str = str.Replace("#sizeItem", strDropdown);

                            }

                        }

                        return Json(new { status = "success", Data = str }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    {
                        return Json(new { status = "error", Data = "No data found." }, JsonRequestBehavior.AllowGet);
                    }
                }
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        [HttpPost]
        public ActionResult PopulateCart()
        {

            try
            {
                string usrID = Session["uID"].ToString();
                return Json(new JsonResult()
                {
                    Data = GetItemAll(usrID).ToList()
                }, JsonRequestBehavior.AllowGet);
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        [HttpPost]
        public ActionResult ViewSelectedProduct(int id)
        {
            try
            {              
                using (linqDBContext db = new linqDBContext())
                {
                    var item = (from us in db.tblItems                                 
                                 select us).FirstOrDefault();
                    if (item != null)
                    {
                        //Latest Side Menu            
                        string content = "";
                        string sectionsPath = Server.MapPath("~/data/templates/" + "quickView" + ".txt");
                        content = System.IO.File.ReadAllText(sectionsPath);

                        string str = "";

                        str += content;
                        str = str.Replace("idHere", item.id + "");
                        str = str.Replace("captionHere", item.itemName);
                       str = str.Replace("codeHere", item.itemCode);
                        str = str.Replace("priceHere", Convert.ToInt32(item.netPrice) + "");
                        str = str.Replace("scaleHere", item.scale + "");
                        str = str.Replace("priceOHere", item.originalPrice + "");                                             
                        str = str.Replace("descHere", item.description + "");
                        if (item.tag == "")
                        {
                            str = str.Replace("label-new", "");
                        }
                        else
                        {
                            str = str.Replace("itemTagHere", item.tag + "");
                        }


                        //Images
                        var pics = (from us in db.tblItemPictures
                                    where us.itemID == id
                                    select us).ToList();
                        if (pics != null)
                        {
                            string picsData = "";

                            string picsContent = "";
                            string sectionsPathPic = Server.MapPath("~/data/templates/" + "quickViewPics" + ".txt");
                            picsContent = System.IO.File.ReadAllText(sectionsPathPic);
                                                     
                            foreach (var picLocation in pics)
                            {
                                picsData += picsContent.Replace("srcHere", picLocation.picLocation);
                            }

                            str = str.Replace("#productImagesHere", picsData + "");
                        }

                        


                        return Json(new { status = "success", Data = str }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    {
                        return Json(new { status = "error", Data = "No data found." }, JsonRequestBehavior.AllowGet);
                    }
                }
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        [HttpPost]
        public ActionResult GetSelectedProductImages(int id)
        {
            try
            {
                using (linqDBContext db = new linqDBContext())
                {                   
                    string picsData = "";
                    var pics = (from us in db.tblItemPictures
                                where us.itemID == id
                                select us).ToList();
                    if (pics != null)
                    {
                        

                        string picsContent = "";
                        string sectionsPathPic = Server.MapPath("~/data/templates/" + "quickViewPics" + ".txt");
                        picsContent = System.IO.File.ReadAllText(sectionsPathPic);

                        foreach (var picLocation in pics)
                        {
                            picsData += picsContent.Replace("srcHere", picLocation.picLocation);
                        }

                        return Json(new { status = "success", Data = picsData }, JsonRequestBehavior.AllowGet);
                    }
                   
                    else
                    {
                        return Json(new { status = "error", Data = "No data found." }, JsonRequestBehavior.AllowGet);
                    }
                }
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        [HttpPost]
        public ActionResult AddToCart(int id, decimal qty, string size)
        {
            try
            {
                string usrID = Session["uID"].ToString();
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

        [HttpPost]
        public ActionResult GetCartID(int id, string size)
        {
            try
            {
                string usrID = Session["uID"].ToString();

                using (linqDBContext db = new linqDBContext())
                {
                    var q = (from us in db.tblCarts
                             join item in db.tblItems on us.itemID equals item.id
                             where us.userMob == usrID
                             && us.itemID == id
                             && us.size == size
                             select new {  cartID = us.id }).FirstOrDefault();
                    if (q != null)
                    {
                        return Json(new { status = "success", Data = q.cartID }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    {
                        return Json(new { status = "success", Data = 0 }, JsonRequestBehavior.AllowGet);
                    }
                    
                }

            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        [HttpPost]
        public ActionResult UpdateCart(int cID, decimal qty, string size)
        {
            try
            {
                string usrID = Session["uID"].ToString();
                decimal quantity = qty;

                using (linqDBContext db = new linqDBContext())
                {

                    var cartQty = (from us in db.tblCarts
                                   where us.userMob == usrID
                                   && us.id == cID                                   
                                   select us).FirstOrDefault();
                    if (cartQty != null)
                    {
                        quantity += cartQty.itemQty.Value;
                    }

                    //Availibity
                    var chkAvailibity = (from us in db.Inventory_Latest
                                         where us.itemId == cartQty.itemID
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
                             && us.id == cID
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
                        return Json(new { status = "error", Data = "Nothing to update" }, JsonRequestBehavior.AllowGet);
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

        public IEnumerable<Object> GetItemAll(string user)
        {
            using (linqDBContext db = new linqDBContext())
            {
                var q = (from us in db.tblItems
                         join c in db.tblCarts on us.id equals c.itemID
                         where c.userMob == user
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
        public ActionResult GetTotal()
        {
            try
            {
                string usrID = Session["uID"].ToString();
                string data = "";
                using (linqDBContext db = new linqDBContext())
                {
                    try
                    {
                        var count = (from us in db.tblCarts
                                     where us.userMob == usrID
                                     select us.itemID.Value).Distinct().Count();
                        if (count > 0)
                        {
                            data = count + " ITEMS";
                        }
                        else
                        {
                            data = "0 ITEM";
                        }
                    }
                    catch (Exception x)
                    {
                        data = "0 ITEM";
                    }

                    try
                    {
                        //Total
                        var total = (from us in db.tblCarts
                                     where us.userMob == usrID
                                     select us.Price.Value).Sum();
                        if (total > 0)
                        {
                            data += "-" + total;
                        }
                        else
                        {
                            data += "-" + 0;
                        }
                    }
                    catch (Exception)
                    {
                        data += "-" + 0;
                    }


                    return Json(new { status = "success", Data = data }, JsonRequestBehavior.AllowGet);
                }
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        [HttpPost]
        public ActionResult ShowInv(int itemID)
        {
            try
            {
                string data = "";       
                using (linqDBContext db = new linqDBContext())
                {
                    try
                    {
                        var invQuery = (from us in db.Inventory_Latest
                                        where us.itemId == itemID
                                        && us.network == 3
                                        && us.localID == 0                                        
                                        select us).ToList();
                        if (invQuery != null)
                        {
                            foreach (var item in invQuery)
                            {
                                data += item.size + "(" + item.qty + ") ";
                            }
                        }

                        //Old
                        //var invQuery = (from us in db.tblSubInventories
                        //                where us.itemId == itemID
                        //                && us.issueRetailId == 1
                        //                select us).FirstOrDefault();
                        //if (invQuery != null)
                        //{
                        //    data = "S (" + invQuery.small.Value.ToString() + ") M (" + invQuery.medium.Value.ToString() + ") L (" + invQuery.large.Value.ToString() + ") XL (" + invQuery.xLarge.Value.ToString() + ")";
                        //}
                    }
                    catch (Exception)
                    {
                        data = "";
                    }


                    return Json(new { status = "success", Data = data }, JsonRequestBehavior.AllowGet);
                }
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        [HttpPost]
        public ActionResult RemoveFromCart(int id)
        {
            try
            {
                string usrID = Session["uID"].ToString();

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
        public ActionResult ClearCart()
        {
            try
            {
                string usrID = Session["uID"].ToString();
                using (linqDBContext db = new linqDBContext())
                {
                    var data = (from us in db.tblCarts
                                where us.userMob == usrID
                                select us).ToList();
                    if (data != null)
                    {
                        foreach (var item in data)
                        {
                            db.tblCarts.Remove(item);
                            db.SaveChanges();
                        }
                    }

                    return Json(new { status = "success", Data = "done" }, JsonRequestBehavior.AllowGet);
                }
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        public static string GetTotalCount(string usrID)
        {
            try
            {
                string data = "";
                using (linqDBContext db = new linqDBContext())
                {
                    try
                    {
                        var count = (from us in db.tblCarts
                                     where us.userMob == usrID
                                     select us).Distinct().Count();
                        if (count > 0)
                        {
                            data = count.ToString();
                        }
                        else
                        {
                            data = "0";
                        }
                    }
                    catch (Exception x)
                    {
                        data = "0";
                    }

                    try
                    {
                        //Total
                        var total = (from us in db.tblCarts
                                     where us.userMob == usrID
                                     select us.Price.Value).Sum();
                        if (total > 0)
                        {
                            data += "-" + total;
                        }
                        else
                        {
                            data += "-" + 0;
                        }
                    }
                    catch (Exception)
                    {
                        data += "-" + 0;
                    }


                    return data;
                }
            }
            catch (Exception x)
            {
                return "0-0";
            }
        }

        public ActionResult ShowReport(string dtFrom, string dtTo)
        {

            SqlConnection con = new SqlConnection(Program.conStr);
            DataTable dt = new DataTable();
            int userID = Convert.ToInt32(Session["uID"]);
            try
            {
                string[] dFrom = dtFrom.Split('-');
                dtFrom = "";
                dtFrom = dFrom[1] + "/" + dFrom[2] + "/" + dFrom[0];
                string[] dTo = dtTo.Split('-');
                dtTo = "";
                dtTo = dTo[1] + "/" + dTo[2] + "/" + dTo[0];

                string fr = "";
                string note = "";

                ReportDocument cryRpt = new ReportDocument();
                TableLogOnInfos crtableLogoninfos = new TableLogOnInfos();
                TableLogOnInfo crtableLogoninfo = new TableLogOnInfo();
                ConnectionInfo crConnectionInfo = new ConnectionInfo();
                Tables CrTables;

                cryRpt.Load(Server.MapPath("~/Reports/sharesReport.rpt"));
                fr = "{tblOrderSummary.paid} > 0 and {tblOrderSummary.status} = 'Delievered' and {tblOrderSummary.userID} = " + userID + " and {tblOrderSummary.date} >=" + "#" + dtFrom + "# and {tblOrderSummary.date} <=" + "#" + dtTo + "#";



                crConnectionInfo.ServerName = Program.sqlServer;
                crConnectionInfo.DatabaseName = Program.sqlDB;
                crConnectionInfo.UserID = Program.sqlUer;
                crConnectionInfo.Password = Program.sqlPw;
                CrTables = cryRpt.Database.Tables;
                foreach (CrystalDecisions.CrystalReports.Engine.Table CrTable in CrTables)
                {
                    crtableLogoninfo = CrTable.LogOnInfo;
                    crtableLogoninfo.ConnectionInfo = crConnectionInfo;
                    CrTable.ApplyLogOnInfo(crtableLogoninfo);
                }

                //Parameters       
                cryRpt.RecordSelectionFormula = fr;
                cryRpt.SetParameterValue("from", dtFrom);
                cryRpt.SetParameterValue("to", dtTo);



                
                return new CrystalReportPdfResult(cryRpt);              

            }
            catch (Exception ex)
            {
                using (StreamWriter _testData = new StreamWriter(Server.MapPath("~/data.txt"), true))
                {
                    _testData.WriteLine("Error on share report: " + Environment.NewLine + ex.Message); // Write the file.
                }
                return Json(new { status = "error", message = ex.Message });
            }
        }

        public ActionResult ShowReportSup(string dtFrom, string dtTo, string res) //Reseller share report
        {            
            int resllerID = Convert.ToInt32(res);
            SqlConnection con = new SqlConnection(Program.conStr);
            DataTable dt = new DataTable();
            int userID = Convert.ToInt32(Session["uID"]);
            try
            {
                string[] dFrom = dtFrom.Split('-');
                dtFrom = "";
                dtFrom = dFrom[1] + "/" + dFrom[2] + "/" + dFrom[0];
                string[] dTo = dtTo.Split('-');
                dtTo = "";
                dtTo = dTo[1] + "/" + dTo[2] + "/" + dTo[0];

                string fr = "";
                string note = "";

                ReportDocument cryRpt = new ReportDocument();
                TableLogOnInfos crtableLogoninfos = new TableLogOnInfos();
                TableLogOnInfo crtableLogoninfo = new TableLogOnInfo();
                ConnectionInfo crConnectionInfo = new ConnectionInfo();
                Tables CrTables;

                cryRpt.Load(Server.MapPath("~/Reports/sharesReport.rpt"));
                if (resllerID > 0) //Selected reseller
                {                   
                    fr = "{tblOrderSummary.paid} > 0 and {tblOrderSummary.status} = 'Delievered' and {tblOrderSummary.date} >=" + "#" + dtFrom + "# and {tblOrderSummary.date} <=" + "#" + dtTo + "#  and {tblReseller.id} = " + resllerID;

                }
                else
                {
                    fr = "{tblOrderSummary.paid} > 0 and {tblOrderSummary.status} = 'Delievered' and {tblOrderSummary.date} >=" + "#" + dtFrom + "# and {tblOrderSummary.date} <=" + "#" + dtTo + "#";
                }



                crConnectionInfo.ServerName = Program.sqlServer;
                crConnectionInfo.DatabaseName = Program.sqlDB;
                crConnectionInfo.UserID = Program.sqlUer;
                crConnectionInfo.Password = Program.sqlPw;
                CrTables = cryRpt.Database.Tables;
                foreach (CrystalDecisions.CrystalReports.Engine.Table CrTable in CrTables)
                {
                    crtableLogoninfo = CrTable.LogOnInfo;
                    crtableLogoninfo.ConnectionInfo = crConnectionInfo;
                    CrTable.ApplyLogOnInfo(crtableLogoninfo);
                }

                //Parameters       
                cryRpt.RecordSelectionFormula = fr;
                cryRpt.SetParameterValue("from", dtFrom);
                cryRpt.SetParameterValue("to", dtTo);




                return new CrystalReportPdfResult(cryRpt);

            }
            catch (Exception ex)
            {
                using (StreamWriter _testData = new StreamWriter(Server.MapPath("~/data.txt"), true))
                {
                    _testData.WriteLine("Error on share report: " + Environment.NewLine + ex.Message); // Write the file.
                }
                return Json(new { status = "error", message = ex.Message });
            }
        }

        public ActionResult ShowReportSupervisor(string dtFrom, string dtTo, string sup) //Supervisor share
        {            
            int supervisor = Convert.ToInt32(sup);
            SqlConnection con = new SqlConnection(Program.conStr);
            DataTable dt = new DataTable();
            int userID = Convert.ToInt32(Session["uID"]);
            try
            {
                string[] dFrom = dtFrom.Split('-');
                dtFrom = "";
                dtFrom = dFrom[1] + "/" + dFrom[2] + "/" + dFrom[0];
                string[] dTo = dtTo.Split('-');
                dtTo = "";
                dtTo = dTo[1] + "/" + dTo[2] + "/" + dTo[0];

                string fr = "";
                string note = "";

                ReportDocument cryRpt = new ReportDocument();
                TableLogOnInfos crtableLogoninfos = new TableLogOnInfos();
                TableLogOnInfo crtableLogoninfo = new TableLogOnInfo();
                ConnectionInfo crConnectionInfo = new ConnectionInfo();
                Tables CrTables;

                cryRpt.Load(Server.MapPath("~/Reports/sharesReport-sup.rpt"));
                if (supervisor > 0)
                {
                    if (supervisor == 26 || supervisor == 27)
                    {
                        fr = "{tblOrderSummary.paid} > 0 and {tblOrderSummary.status} = 'Delievered' and {tblOrderSummary.date} >=" + "#" + dtFrom + "# and {tblOrderSummary.date} <=" + "#" + dtTo + "# and {tblReseller.supervisor} in [26,27]";

                    }
                    else
                    {
                        fr = "{tblOrderSummary.paid} > 0 and {tblOrderSummary.status} = 'Delievered' and {tblOrderSummary.date} >=" + "#" + dtFrom + "# and {tblOrderSummary.date} <=" + "#" + dtTo + "# and {tblReseller.supervisor} = " + supervisor;

                    }
                }
                else
                {
                    fr = "{tblOrderSummary.paid} > 0 and {tblOrderSummary.status} = 'Delievered' and {tblOrderSummary.date} >=" + "#" + dtFrom + "# and {tblOrderSummary.date} <=" + "#" + dtTo + "#";
                }




                crConnectionInfo.ServerName = Program.sqlServer;
                crConnectionInfo.DatabaseName = Program.sqlDB;
                crConnectionInfo.UserID = Program.sqlUer;
                crConnectionInfo.Password = Program.sqlPw;
                CrTables = cryRpt.Database.Tables;
                foreach (CrystalDecisions.CrystalReports.Engine.Table CrTable in CrTables)
                {
                    crtableLogoninfo = CrTable.LogOnInfo;
                    crtableLogoninfo.ConnectionInfo = crConnectionInfo;
                    CrTable.ApplyLogOnInfo(crtableLogoninfo);
                }

                //Parameters       
                cryRpt.RecordSelectionFormula = fr;
                cryRpt.SetParameterValue("from", dtFrom);
                cryRpt.SetParameterValue("to", dtTo);




                return new CrystalReportPdfResult(cryRpt);

            }
            catch (Exception ex)
            {
                using (StreamWriter _testData = new StreamWriter(Server.MapPath("~/data.txt"), true))
                {
                    _testData.WriteLine("Error on share report: " + Environment.NewLine + ex.Message); // Write the file.
                }
                return Json(new { status = "error", message = ex.Message });
            }
        }

        public ActionResult ShowInvoice(int id)
        {

            SqlConnection con = new SqlConnection(Program.conStr);
            DataTable dt = new DataTable();
            int userID = Convert.ToInt32(Session["uID"]);
            try
            {

                ReportDocument cryRpt = new ReportDocument();
                TableLogOnInfos crtableLogoninfos = new TableLogOnInfos();
                TableLogOnInfo crtableLogoninfo = new TableLogOnInfo();
                ConnectionInfo crConnectionInfo = new ConnectionInfo();
                Tables CrTables;

                cryRpt.Load(Server.MapPath("~/Reports/invoice.rpt"));
                 string fr = "{tblOrderSummary.id} = " + id;



                crConnectionInfo.ServerName = Program.sqlServer;
                crConnectionInfo.DatabaseName = Program.sqlDB;
                crConnectionInfo.UserID = Program.sqlUer;
                crConnectionInfo.Password = Program.sqlPw;
                CrTables = cryRpt.Database.Tables;
                foreach (CrystalDecisions.CrystalReports.Engine.Table CrTable in CrTables)
                {
                    crtableLogoninfo = CrTable.LogOnInfo;
                    crtableLogoninfo.ConnectionInfo = crConnectionInfo;
                    CrTable.ApplyLogOnInfo(crtableLogoninfo);
                }

                
                cryRpt.RecordSelectionFormula = fr;
                return new CrystalReportPdfResult(cryRpt);

            }
            catch (Exception ex)
            {
                using (StreamWriter _testData = new StreamWriter(Server.MapPath("~/data.txt"), true))
                {
                    _testData.WriteLine("Error on share report: " + Environment.NewLine + ex.Message); // Write the file.
                }
                return Json(new { status = "error", message = ex.Message });
            }
        }

        public ActionResult ShowSummary(string dtFrom, string dtTo, string status, string dBy)
        {
            DateTime dfrom = Convert.ToDateTime(Convert.ToDateTime(dtFrom).ToShortDateString());
            DateTime dto = Convert.ToDateTime(Convert.ToDateTime(dtTo).ToShortDateString());

            SqlConnection con = new SqlConnection(Program.conStr);
            DataTable dt = new DataTable();
            int userID = Convert.ToInt32(Session["uID"]);
            try
            {
                string[] dFrom = dtFrom.Split('-');
                dtFrom = "";
                dtFrom = dFrom[1] + "/" + dFrom[2] + "/" + dFrom[0];
                string[] dTo = dtTo.Split('-');
                dtTo = "";
                dtTo = dTo[1] + "/" + dTo[2] + "/" + dTo[0];

                string fr = "";
                string note = "";

                ReportDocument cryRpt = new ReportDocument();
                TableLogOnInfos crtableLogoninfos = new TableLogOnInfos();
                TableLogOnInfo crtableLogoninfo = new TableLogOnInfo();
                ConnectionInfo crConnectionInfo = new ConnectionInfo();
                Tables CrTables;

                cryRpt.Load(Server.MapPath("~/Reports/summaryReport.rpt"));
                if (status != "0")
                {
                    fr += "{tblOrderSummary.status} = '" + status + "' and ";
                    note = "Status (" + status + ")";
                }
                else
                {
                    note = "Status (" + "All" + ")";
                }
                if (dBy != "0")
                {
                    fr += "{tblOrderSummary.deliveryOption} = '" + dBy + "' and ";
                    note += "& Delivered by (" + dBy + ")";
                }
                else
                {
                    note += " & Delivered by (" + "All" + ")";
                }

                fr += "{tblOrderSummary.date} >=" + "#" + dtFrom + "# and {tblOrderSummary.date} <=" + "#" + dtTo + "#";



                crConnectionInfo.ServerName = Program.sqlServer;
                crConnectionInfo.DatabaseName = Program.sqlDB;
                crConnectionInfo.UserID = Program.sqlUer;
                crConnectionInfo.Password = Program.sqlPw;
                CrTables = cryRpt.Database.Tables;
                foreach (CrystalDecisions.CrystalReports.Engine.Table CrTable in CrTables)
                {
                    crtableLogoninfo = CrTable.LogOnInfo;
                    crtableLogoninfo.ConnectionInfo = crConnectionInfo;
                    CrTable.ApplyLogOnInfo(crtableLogoninfo);
                }

                string catInv = "";
                try //Category wise Inventory
                {
                    using (linqDBContext db = new linqDBContext())
                    {                      
                        var qInv = (from us in db.tblItemsSolds
                                    join sum in db.tblOrderSummaries
                                    on us.invoiceNum equals sum.id
                                    join items in db.tblItems
                                    on us.itemID equals items.id
                                    join cat in db.tblMainCategories
                                    on items.mainCategory equals cat.id
                                    where sum.date >= dfrom && sum.date <= dto
                                    select new { catID = cat.id, catName = cat.name, qty = us.quantity, status = sum.status, dBy = sum.deliveryOption }).ToList();
                       
                        if (status != "0")
                        {
                            qInv = (from us in db.tblItemsSolds
                                    join sum in db.tblOrderSummaries
                                    on us.invoiceNum equals sum.id
                                    join items in db.tblItems
                                    on us.itemID equals items.id
                                    join cat in db.tblMainCategories
                                    on items.mainCategory equals cat.id
                                    where sum.date >= dfrom && sum.date <= dto
                                    && sum.status == status
                                    select new { catID = cat.id, catName = cat.name, qty = us.quantity, status = sum.status, dBy = sum.deliveryOption }).ToList();

                        }
                        if (dBy != "0")
                        {
                            qInv = (from us in db.tblItemsSolds
                                    join sum in db.tblOrderSummaries
                                    on us.invoiceNum equals sum.id
                                    join items in db.tblItems
                                    on us.itemID equals items.id
                                    join cat in db.tblMainCategories
                                    on items.mainCategory equals cat.id
                                    where sum.date >= dfrom && sum.date <= dto
                                    && sum.deliveryOption == dBy
                                    select new { catID = cat.id, catName = cat.name, qty = us.quantity, status = sum.status, dBy = sum.deliveryOption }).ToList();

                        }


                        if (qInv != null)
                        {
                            var dCats = qInv.Select(x => x.catID).Distinct();
                            if (dCats != null)
                            {
                                foreach (var cItem in dCats)
                                {
                                    var name = qInv.Where(x => x.catID == cItem).Select(y => y.catName).FirstOrDefault();
                                    var qty = qInv.Where(x => x.catID == cItem).Select(y => y.qty).Sum();
                                    if (qty > 0)
                                    {
                                        catInv += name + ": " + Convert.ToInt32(qty) + Environment.NewLine;
                                    }
                                }
                            }
                        }

                    }
                }
                catch (Exception x)
                { }

                //Parameters       
                cryRpt.RecordSelectionFormula = fr;
               
                string range = dtFrom.Split('/')[1] + "-" + dtFrom.Split('/')[0] + "-" + dtFrom.Split('/')[2];
                range += " to ";
                range += dtTo.Split('/')[1] + "-" + dtTo.Split('/')[0] + "-" + dtTo.Split('/')[2];
                cryRpt.SetParameterValue("range", range);
                cryRpt.SetParameterValue("options", note);
                cryRpt.SetParameterValue("inv", catInv);


                return new CrystalReportPdfResult(cryRpt);

            }
            catch (Exception ex)
            {
                using (StreamWriter _testData = new StreamWriter(Server.MapPath("~/data.txt"), true))
                {
                    _testData.WriteLine("Error on share report: " + Environment.NewLine + ex.Message); // Write the file.
                }
                return Json(new { status = "error", message = ex.Message });
            }
        }

        public ActionResult ShowSummaryM(string txtFrom, string txtTo)
        {
            int from = Convert.ToInt32(txtFrom);
            int to = Convert.ToInt32(txtTo);

            SqlConnection con = new SqlConnection(Program.conStr);
            DataTable dt = new DataTable();
            int userID = Convert.ToInt32(Session["uID"]);
            try
            {              
                string fr = "";
                string note = "";

                ReportDocument cryRpt = new ReportDocument();
                TableLogOnInfos crtableLogoninfos = new TableLogOnInfos();
                TableLogOnInfo crtableLogoninfo = new TableLogOnInfo();
                ConnectionInfo crConnectionInfo = new ConnectionInfo();
                Tables CrTables;

                cryRpt.Load(Server.MapPath("~/Reports/invoice.rpt"));
                note = "Invoice # (" + txtFrom + ") - ";
                note += "(" + txtTo + ")";
                string arr = "";                
                for (int i = from; i <= to; i++)
                {
                    arr += i + ",";
                }
                arr = arr.Substring(0, arr.Length - 1);

                fr = "{tblOrderSummary.id} in [" + arr + "]";



                crConnectionInfo.ServerName = Program.sqlServer;
                crConnectionInfo.DatabaseName = Program.sqlDB;
                crConnectionInfo.UserID = Program.sqlUer;
                crConnectionInfo.Password = Program.sqlPw;
                CrTables = cryRpt.Database.Tables;
                foreach (CrystalDecisions.CrystalReports.Engine.Table CrTable in CrTables)
                {
                    crtableLogoninfo = CrTable.LogOnInfo;
                    crtableLogoninfo.ConnectionInfo = crConnectionInfo;
                    CrTable.ApplyLogOnInfo(crtableLogoninfo);
                }

                //Parameters       
                cryRpt.RecordSelectionFormula = fr;
           
               


                return new CrystalReportPdfResult(cryRpt);

            }
            catch (Exception ex)
            {
                using (StreamWriter _testData = new StreamWriter(Server.MapPath("~/data.txt"), true))
                {
                    _testData.WriteLine("Error on share report: " + Environment.NewLine + ex.Message); // Write the file.
                }
                return Json(new { status = "error", message = ex.Message });
            }
        }

        public ActionResult ShowInv()
        {

            SqlConnection con = new SqlConnection(Program.conStr);
            DataTable dt = new DataTable();
            int userID = Convert.ToInt32(Session["uID"]);
            try
            {
                Program.createTable("drop table temp_inv");
                Program.createTable("DECLARE @cols AS NVARCHAR(MAX), @query AS NVARCHAR(MAX) select @cols = STUFF((SELECT ',' + QUOTENAME(size) from Inventory_Latest group by size order by size FOR XML PATH(''), TYPE ).value('.', 'NVARCHAR(MAX)') ,1,1,'') set @query = 'SELECT id = IDENTITY(INT, 1, 1), itemId,' + @cols + ' into temp_inv from ( select itemId, qty, size from Inventory_Latest where network = 3 and localID = 0 and qty > 0) x pivot ( sum(qty) for size in (' + @cols + ') ) p ' execute(@query);");
                Program.createTable("ALTER TABLE temp_inv ADD PRIMARY KEY ( id );");

                string fr = "";
                string note = "";

                ReportDocument cryRpt = new ReportDocument();
                TableLogOnInfos crtableLogoninfos = new TableLogOnInfos();
                TableLogOnInfo crtableLogoninfo = new TableLogOnInfo();
                ConnectionInfo crConnectionInfo = new ConnectionInfo();
                Tables CrTables;

                cryRpt.Load(Server.MapPath("~/Reports/inv_latest.rpt"));

                crConnectionInfo.ServerName = Program.sqlServer;
                crConnectionInfo.DatabaseName = Program.sqlDB;
                crConnectionInfo.UserID = Program.sqlUer;
                crConnectionInfo.Password = Program.sqlPw;
                CrTables = cryRpt.Database.Tables;
                foreach (CrystalDecisions.CrystalReports.Engine.Table CrTable in CrTables)
                {
                    crtableLogoninfo = CrTable.LogOnInfo;
                    crtableLogoninfo.ConnectionInfo = crConnectionInfo;
                    CrTable.ApplyLogOnInfo(crtableLogoninfo);
                }

                string catInv = "";
                try //Category wise Inventory
                {
                    using (linqDBContext db = new linqDBContext())
                    {
                        var qInv = (from us in db.Inventory_Latest
                                    join items in db.tblItems
                                    on us.itemId equals items.id
                                    join cat in db.tblMainCategories
                                    on items.mainCategory equals cat.id
                                    select new { catID = cat.id, catName = cat.name, qty = us.qty }).ToList();
                        if (qInv != null)
                        {
                            var dCats = qInv.Select(x => x.catID).Distinct();
                            if (dCats != null)
                            {
                                foreach (var cItem in dCats)
                                {
                                    var name = qInv.Where(x => x.catID == cItem).Select(y => y.catName).FirstOrDefault();
                                    var qty = qInv.Where(x => x.catID == cItem).Select(y => y.qty).Sum();
                                    if (qty > 0)
                                    {
                                        catInv += name + ": " + qty + Environment.NewLine; 
                                    }
                                }
                            }
                        }
                    }
                }
                catch (Exception x)
                { }

                //Parameters       
                cryRpt.RecordSelectionFormula = fr;

                cryRpt.SetParameterValue("range", "Inventory report as on");
                cryRpt.SetParameterValue("options", DateTime.Now.Day + "-" + DateTime.Now.Month + "-" + DateTime.Now.Year);
                cryRpt.SetParameterValue("inv", catInv);

                return new CrystalReportPdfResult(cryRpt);

            }
            catch (Exception ex)
            {
                using (StreamWriter _testData = new StreamWriter(Server.MapPath("~/data.txt"), true))
                {
                    _testData.WriteLine("Error on share report: " + Environment.NewLine + ex.Message); // Write the file.
                }
                return Json(new { status = "error", message = ex.Message });
            }
        }

        public ActionResult ShowInv_Old()
        {
           
            SqlConnection con = new SqlConnection(Program.conStr);
            DataTable dt = new DataTable();
            int userID = Convert.ToInt32(Session["uID"]);
            try
            {
                string fr = "";
                string note = "";

                ReportDocument cryRpt = new ReportDocument();
                TableLogOnInfos crtableLogoninfos = new TableLogOnInfos();
                TableLogOnInfo crtableLogoninfo = new TableLogOnInfo();
                ConnectionInfo crConnectionInfo = new ConnectionInfo();
                Tables CrTables;

                cryRpt.Load(Server.MapPath("~/Reports/inv.rpt"));
               

                fr = "({tblSubInventory.networkID} = 3 and {tblSubInventory.small} > 0) or ";
                fr += "({tblSubInventory.networkID} = 3 and {tblSubInventory.medium} > 0) or ";
                fr += "({tblSubInventory.networkID} = 3 and {tblSubInventory.large} > 0) or ";
                fr += "({tblSubInventory.networkID} = 3 and {tblSubInventory.xLarge} > 0)";

                crConnectionInfo.ServerName = Program.sqlServer;
                crConnectionInfo.DatabaseName = Program.sqlDB;
                crConnectionInfo.UserID = Program.sqlUer;
                crConnectionInfo.Password = Program.sqlPw;
                CrTables = cryRpt.Database.Tables;
                foreach (CrystalDecisions.CrystalReports.Engine.Table CrTable in CrTables)
                {
                    crtableLogoninfo = CrTable.LogOnInfo;
                    crtableLogoninfo.ConnectionInfo = crConnectionInfo;
                    CrTable.ApplyLogOnInfo(crtableLogoninfo);
                }

                //Parameters       
                cryRpt.RecordSelectionFormula = fr;

                cryRpt.SetParameterValue("range", "Inventory report as on");
                cryRpt.SetParameterValue("options", DateTime.Now.Day + "-" + DateTime.Now.Month + "-" + DateTime.Now.Year);


                return new CrystalReportPdfResult(cryRpt);

            }
            catch (Exception ex)
            {
                using (StreamWriter _testData = new StreamWriter(Server.MapPath("~/data.txt"), true))
                {
                    _testData.WriteLine("Error on share report: " + Environment.NewLine + ex.Message); // Write the file.
                }
                return Json(new { status = "error", message = ex.Message });
            }
        }


    }

}