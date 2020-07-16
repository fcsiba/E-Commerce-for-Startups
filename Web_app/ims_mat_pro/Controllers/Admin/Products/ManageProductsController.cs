using ims_mat_pro.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Linq.Dynamic;
using ims_mat_pro.Models.ManageProducts;
using System.IO;

namespace ims_mat_pro.Controllers.Products
{
    public class ManageProductsController : Controller
    {
        linqDBContext dc = new linqDBContext();
        List<SelectListItem> maincat, subCat, lotx;
        // GET: ProductsOverview
        public ActionResult Index()
        {
            populateDropdown();
            return View();
        }

        public ActionResult Add()
        {
            populateDropdown();

            ViewBag.cat = subCat;
            ViewBag.maincat = maincat;
            ViewBag.lotD = lotx;


            data d = new data();
            
            return View(d);
        }


        public ActionResult Edit(int id)
        {
            populateDropdown();
            ViewBag.cat = subCat;
            ViewBag.maincat = maincat;
            ViewBag.lotD = lotx;

            ViewBag.pID = id;

            using (linqDBContext db = new linqDBContext())
            {
                var category = (from us in db.tblCategories
                                select us).ToList();

                List<SelectListItem> item = new List<SelectListItem>();
                foreach (var itemm in category)
                {
                    item.Add(new SelectListItem
                    {
                        Text = itemm.name,
                        Value = itemm.id.ToString()
                    });
                }

                ViewBag.cat = item;

                var product = (from us in db.tblItems
                               where us.id == id
                               select us).FirstOrDefault();
                if (product != null)
                {
                    ViewBag.product = product;
                }
                
            }

            data d = new data();

            return View(d);
        }

        public ActionResult GetImages(int id)
        {
            try
            {
                using (linqDBContext db = new linqDBContext())
                {
                    var images = (from us in db.tblItemPictures
                                  where us.itemID == id
                                  select new { us.id, pic =  us.picLocation }).ToList();
                    if (images != null)
                    {
                        return Json(new { status = "success", Data = images }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    {
                        return Json(new { status = "success", Data = "" }, JsonRequestBehavior.AllowGet);
                    }
                }
               
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        public ActionResult RemoveImage(int imageID)
        {
            try
            {
                using (linqDBContext db = new linqDBContext())
                {
                    var images = (from us in db.tblItemPictures
                                  where us.id == imageID
                                  select us).FirstOrDefault();
                    if (images != null)
                    {
                        db.tblItemPictures.Remove(images);
                        db.SaveChanges();
                        return Json(new { status = "success", Data = "done" }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    {
                        return Json(new { status = "error", Data = "No image found" }, JsonRequestBehavior.AllowGet);
                    }
                }

            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }


        public ActionResult AddProduct(string code, string name, string fabric, string priceO, string discount, string priceNet, int cat,string mcat,int lotId,string articleId, string desc, string[] picsArr)
        {
            try
            {
                int assignedID = 0;
                using (linqDBContext db = new linqDBContext())
                {

                    int getCatId = (from a in db.tblMainCategories where a.name == mcat select a.id).FirstOrDefault();


                    tblItem it = new tblItem();
                    it.itemCode = code;                   
                    it.itemName = name;
                    it.fabric = fabric;
                    it.originalPrice = Convert.ToDecimal(priceO);
                    it.discount = Convert.ToDecimal(discount);
                    it.netPrice = Convert.ToDecimal(priceNet);
                    it.subCategory = cat;
                    it.mainCategory = getCatId;
                    it.description = desc;                    
                    it.tag = "";
                    it.articleId = articleId;
                    it.lotId = lotId;
                    it.date = Convert.ToDateTime(DateTime.Now.ToShortDateString());
                    db.tblItems.Add(it);
                    db.SaveChanges();
                    assignedID = it.id;


                    if (picsArr != null)
                    {
                        foreach (var item in picsArr)
                        {
                            //var physicalPath = Path.Combine(Server.MapPath(subPath), item);
                            tblItemPicture p = new tblItemPicture();
                            p.itemID = assignedID;
                            p.picLocation = item;
                            db.tblItemPictures.Add(p);
                            db.SaveChanges();
                        }
                    }
                    
                }

                return Json(new JsonResult()
                {
                    Data = assignedID
                }, JsonRequestBehavior.AllowGet);
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        public ActionResult EditProduct(int id, string code, string name, string fabric, string priceO, string discount, string priceNet, int cat,string mcat, string lotId, string articleId, string desc, string[] picsArr)
        {
            try
            {
                int lotIdx;
               lotIdx= Convert.ToInt32(lotId);           
                using (linqDBContext db = new linqDBContext())
                {
                    int getCatId = (from a in db.tblMainCategories where a.name == mcat select a.id).FirstOrDefault();

                    var it = (from us in db.tblItems where us.id == id select us).FirstOrDefault();
                    if (it != null)
                    {
                        it.itemCode = code;
                        it.itemName = name;
                        it.fabric = fabric;
                        it.originalPrice = Convert.ToDecimal(priceO);
                        it.discount = Convert.ToDecimal(discount);
                        it.netPrice = Convert.ToDecimal(priceNet);
                        it.subCategory = cat;
                        it.description = desc;
                        it.mainCategory = getCatId;
                        it.lotId = lotIdx;
                        it.articleId = articleId;                      
                        it.date = Convert.ToDateTime(DateTime.Now.ToShortDateString());                        
                        db.SaveChanges();

                    }

                    if (picsArr != null)
                    {
                        foreach (var item in picsArr)
                        {
                            //var physicalPath = Path.Combine(Server.MapPath(subPath), item);
                            tblItemPicture p = new tblItemPicture();
                            p.itemID = id;
                            p.picLocation = item;
                            db.tblItemPictures.Add(p);
                            db.SaveChanges();
                        }
                    }
                  
                }

                return Json(new JsonResult()
                {
                    Data = id
                }, JsonRequestBehavior.AllowGet);
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }


        public ActionResult UpdateCover(int id, string cover)
        {
            try
            {
                using (linqDBContext db = new linqDBContext())
                {
                    var it = (from us in db.tblItems where us.id == id select us).FirstOrDefault();
                    if (it != null)
                    {
                        it.coverImage = cover;
                        db.SaveChanges();
                        return Json(new { status = "success", Data = "Done" }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    {
                        return Json(new { status = "error", Data = "No item found." }, JsonRequestBehavior.AllowGet);
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
                    var chk = (from us in db.tblItemsSolds
                               where us.itemID == id
                               select us).FirstOrDefault();
                    if (chk == null)
                    {
                        var pics = (from us in db.tblItemPictures
                                    where us.itemID == id
                                    select us).ToList();
                        if (pics != null)
                        {
                            foreach (var item in pics)
                            {
                                db.tblItemPictures.Remove(item);
                                db.SaveChanges();
                            }
                        }

                        var data = (from us in db.tblItems
                                    where us.id == id
                                    select us).FirstOrDefault();
                        if (data != null)
                        {
                            db.tblItems.Remove(data);
                            db.SaveChanges();
                        }

                        return Json(new JsonResult()
                        {
                            Data = "Success"
                        }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    {
                        return Json(new { status = "error", Data = "Used in orders!" }, JsonRequestBehavior.AllowGet);
                    }
                }

              
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        public ActionResult UpdateStock(int id, int small, int medium, int large, int xLarge)
        {
            try
            {               
                using (linqDBContext db = new linqDBContext())
                {
                    var data = db.tblItems.Where(m => m.id == id).FirstOrDefault();
                    if (data != null)
                    {
                        //data.small += small;
                        //data.medium += medium;
                        //data.large += large;
                        //data.xLarge += xLarge;
                        db.SaveChanges();

                        //Save Stock History --> Later


                        return Json(new { status = "Success", Data = "Stock Updated!" }, JsonRequestBehavior.AllowGet);


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



        [HttpPost]
        public ActionResult Upload( IEnumerable<HttpPostedFileBase> files)
        {
            string fileNm = "";
            string subPath = @"~/Uploads/Products";
            try
            {                
                if (files.Count() > 0)
                {

                    bool exists = Directory.Exists(Server.MapPath(subPath));
                    if (!exists)
                        Directory.CreateDirectory(Server.MapPath(subPath));

                    foreach (var file in files)
                    {
                        
                        fileNm = Guid.NewGuid().ToString() + System.IO.Path.GetExtension(file.FileName);        

                        //fileNm = checkFileName(fileNm, fileNm);
                        var physicalPath = Path.Combine(Server.MapPath(subPath), fileNm);                     
                        file.SaveAs(physicalPath);

                        string copyToPath = @"C:\Deploy\shop.saajapparels.net\Uploads\Products\" + fileNm;
                        System.IO.File.Copy(physicalPath, copyToPath);

                    }
                }
            }
            catch (Exception x)
            {
                return Json(new { status = "Error", message = x.Message });
            }
            return Json(new { status = "success", message = fileNm }, JsonRequestBehavior.AllowGet);

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
                    var v = (from a in dc.tblItems
                             join b in dc.tblCategories                            
                             on a.subCategory equals b.id
                             join l in dc.Lots
                             on a.lotId equals l.id
                             select new { a.id, category = b.name, Lot = l.lotName, Code = a.itemCode,  a.itemName, Fabric = a.fabric, Price = a.netPrice.Value });

                    //SORT
                    if (!(string.IsNullOrEmpty(sortColumn) && string.IsNullOrEmpty(sortColumnDir)))
                    {
                        v = v.OrderBy(sortColumn + " " + sortColumnDir);
                    }

                    //Search   
                    var searchValue = Request.Form.GetValues("search[value]").FirstOrDefault();
                    if (!string.IsNullOrEmpty(searchValue))
                    {
                        v = v.Where(m => m.itemName.Contains(searchValue) || 
                                    m.Code.ToString().Contains(searchValue));
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

        //Populate Drop down items
        public void populateDropdown()
        {

            using (linqDBContext db = new linqDBContext())
            {
                var category = (from us in db.tblCategories
                                select us).ToList();
     

                var lot = (from us in db.Lots
                           select new { us.id, us.lotName }).ToList();
                List<SelectListItem> item = new List<SelectListItem>();


                List<SelectListItem> lots = new List<SelectListItem>();

                foreach (var itemm in category)
                {
                    item.Add(new SelectListItem
                    {
                        Text = itemm.name,
                        Value = itemm.id.ToString()
                    });
                }

       
                foreach (var itemm in lot)
                {
                    lots.Add(new SelectListItem
                    {
                        Text = itemm.lotName,
                        Value = itemm.id.ToString()
                    });
                }

                
                subCat = item;
                lotx = lots;
                    
                    
                    
                 

            }
            //end populate method

            }


        //populate dropdown
        public JsonResult itemList(string Id)
        {

            int id_ = 0;
            try
            {
                id_ = Convert.ToInt32(Id);
            }
            catch (Exception x)
            {}


            try
            {
                var item = (from s in dc.tblMainCategories 
                            join x in dc.Lots on s.id equals x.mainCatId
                            where x.id == id_
                            select s).FirstOrDefault();

                if (item==null)
                {
                    throw new Exception("Category Not Found");
                }

                return Json(new JsonResult()
                {
                    Data = item.name
                   
                }, JsonRequestBehavior.AllowGet);
            }
            catch (Exception x)
            {

                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }


    }
}