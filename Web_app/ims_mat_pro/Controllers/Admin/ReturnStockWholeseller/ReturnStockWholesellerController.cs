using ims_mat_pro.Models;
using System;
using System.Linq;
using System.Web.Mvc;
using System.Linq.Dynamic;
using System.Collections.Generic;

namespace ims_mat_pro.Controllers.ReturnStock
{
    public class ReturnStockWholesellerController : Controller
    {
        linqDBContext dc = new linqDBContext();
        // GET: ReturnStock
        public ActionResult Index()
        {
        


            return View();
        }


        //Add

            [HttpGet]
        public ActionResult add()
        {
            var T = (from x in dc.tblRetails where x.id==6 || x.id==7 || x.id == 4
                     select x).ToList();

            var C = (from x in dc.tblMainCategories
                     select x).ToList();


            List<SelectListItem> typelist = new List<SelectListItem>();
            List<SelectListItem> categorylist = new List<SelectListItem>();



            foreach (var itemm in T)
            {
                typelist.Add(new SelectListItem
                {
                    Text = itemm.name,
                    Value = itemm.id.ToString()
                });
            }
            foreach (var itemm in C)
            {
                categorylist.Add(new SelectListItem
                {
                    Text = itemm.name,
                    Value = itemm.id.ToString()
                });
            }


            ViewBag.type = typelist;
            ViewBag.category = categorylist;

            stockView sv = new stockView();
            
            return View(sv);
        }

        [HttpPost]
        public ActionResult add(string retailID_, string localID_, string categoryID_, string pacA, string pacB, string small, string med, string large, string xLarge, string date, string savetomain,string reason)
        {

            tblReturnHistory rh;
            tblReturnInventory tri;
            tblMainInventry tmi;
            tblsubInventoryDetail tsid;

            try
            {
                int retailId = Convert.ToInt32(retailID_);
                int localId = Convert.ToInt32(localID_);
                int categoryId = Convert.ToInt32(categoryID_);
                using (linqDBContext db = new linqDBContext())
                {
                    //Check if any item had been issued to this retiler in 
                    int get_issuedRetailId = (from a in db.issuedRetailers where a.localId == localId && a.retailId == retailId select a.Id).FirstOrDefault();
                    //check if the returned item already exist in the sub inventory with selected category or not if yes then procced to return
                     var chkSubInvDet  = (from a in db.tblsubInventoryDetails
                                     join b in db.tblIssues on a.issueId equals b.id
                                     join c in db.tblLots on b.lotId equals c.id
                                     where c.mainCatId == categoryId select a).FirstOrDefault();

                    if (get_issuedRetailId == 0 || chkSubInvDet==null)
                    {
                        throw new Exception("Sorry , No Item Had Been Issued To This Retailer Or With the Selected Category");
                    }
                    else
                    {
                        int getCatId = (from a in db.tblMainCategories where a.id == categoryId select a.id).FirstOrDefault();

                        var checkReturntable = (from b in db.tblReturnHistories where b.category_Id == getCatId select b).FirstOrDefault();
                        int pacA_ = (Convert.ToInt32(pacA));
                        int pacB_ = (Convert.ToInt32(pacB));
                        int small_ = Convert.ToInt32(small);
                        int medium_ = Convert.ToInt32(med);
                        int large_ = Convert.ToInt32(large);
                        int xlarge_ = Convert.ToInt32(xLarge);

                        // check if category id already exist in return history table if not create new instance of return history
                        if (checkReturntable!=null)
                        {
                            rh = checkReturntable;

                        }
                        else
                        {
                            rh = new tblReturnHistory();
                        }
                        rh.issueRetailId = get_issuedRetailId;
                        rh.category_Id = getCatId;
                         rh.pacA = pacA_;
                        rh.pacB = pacB_;
                        rh.small = small_;
                        rh.medium = medium_;
                        rh.large = large_;
                        rh.xLarge = xlarge_;
                        rh.date = Convert.ToDateTime(date).Date;
                        rh.reason = reason;
                        //  rh.date = date;
                        if (savetomain.Equals("False"))
                        {
                            rh.save_to_main_inventory = false;
                            tri = (from a in db.tblReturnInventories where a.mainCatId == categoryId select a).FirstOrDefault();
                            if (tri == null)
                            {
                                tri = new tblReturnInventory();
                                tri.mainCatId = categoryId;
                                
                                tri.pacA += pacA_;
                                tri.pacB += pacB_;
                                tri.small += small_;
                                tri.medium += medium_;
                                tri.large += large_;
                                tri.xLarge += xlarge_;
                                db.tblReturnInventories.Add(tri);
                                db.SaveChanges();

                            }
                            else
                            {
                                tri.pacA += pacA_;
                                tri.pacB += pacB_;
                                tri.small += small_;
                                tri.medium += medium_;
                                tri.large += large_;
                                tri.xLarge += xlarge_;
                                db.SaveChanges();
                            }

                        }
                        else
                        {
                            rh.save_to_main_inventory = true;
                            tmi= (from a in db.tblMainInventries where a.mainCatId == categoryId select a).FirstOrDefault();

                            if (tmi!=null)
                            {
                                tmi.pacA += pacA_;
                                tmi.pacB += pacB_;
                                tmi.small += small_;
                                tmi.medium += medium_;
                                tmi.large += large_;
                                tmi.xLarge += xlarge_;
                               // db.SaveChanges();
                            }
                            else
                            {
                                throw new Exception("Main Inventory Does'nt have this Category ! Don'nt save in Main Inventory");
                            }

                        }
                        // remove values from subInventory details
                        tsid = (from a in db.tblsubInventoryDetails
                                join b in db.tblIssues on a.issueId equals b.id
                                select a).FirstOrDefault();

                        if (tsid == null)
                        {
                            throw new Exception("Item(s) Doesn't Exist is the Sub Inventory ");

                        }
                        else
                        {
                            ///start fromhere ........
                            tsid.packetA -= pacA_;
                            tsid.packetB -= pacB_;
                            tsid.small -= small_;
                            tsid.medium -= medium_;
                            tsid.large -= large_;
                            tsid.xLarge -= xlarge_;
                        }
                        db.tblReturnHistories.Add(rh);
                        db.SaveChanges();

                    }

                }

                return Json(new JsonResult()
                {
                    Data = "Done"
                }, JsonRequestBehavior.AllowGet);




            }

            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }


        }

        //load data
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
                    var v = from a in abc.tblReturnHistories
                            join b in abc.issuedRetailers on a.issueRetailId equals b.Id
                            join c in abc.tblMainCategories on a.category_Id equals c.id
                            select new { Retailer = b.localName, Category = c.name, pacA = a.pacA, pacB = a.pacB, small = a.small, med = a.medium, large = a.large, xlarge = a.xLarge, date = a.date.Value.Day + "/" + a.date.Value.Month + "/" + a.date.Value.Year, save_to_main_inventory = a.save_to_main_inventory };



                    if (!(string.IsNullOrEmpty(sortColumn) && string.IsNullOrEmpty(sortColumnDir)))
                    {
                        v = v.OrderBy(sortColumn + " " + sortColumnDir);
                    }

                   // Search
                    var searchValue = Request.Form.GetValues("search[value]").FirstOrDefault();
                    if (!string.IsNullOrEmpty(searchValue))
                    {
                        v = v.Where(m => m.Retailer.Contains(searchValue) ||
                                    m.Category.ToString() == searchValue );
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


        //itemlsit
        public JsonResult itemList(string Id)
        {

            int id_ = Convert.ToInt32(Id);

            if (id_ == 1)
            {

                //item = (from s in dc.tbl
                //             where s.lotId == id_
                //             select s).ToList();
            }
            else if (id_ == 2)
            {
                //item = (from s in dc.tbl
                //           where s.lotId == id_
                //          select s).ToList();

            }
            else if (id_ == 3)
            {
                var mainList = (from s in dc.tblResellers
                                select s).ToList();
                return Json(new SelectList(mainList.ToArray(), "id", "name"), JsonRequestBehavior.AllowGet);


            }
            else if (id_ == 4)
            {
                var mainList = (from s in dc.tblDistributors
                                select s).ToList();
                return Json(new SelectList(mainList.ToArray(), "id", "Name"), JsonRequestBehavior.AllowGet);

            }
            else if (id_ == 5)
            {
                var mainList = (from s in dc.tblOutlets
                                select s).ToList();
                return Json(new SelectList(mainList.ToArray(), "id", "OutletName"), JsonRequestBehavior.AllowGet);

            }
            else if (id_ == 6)
            {
                var mainList = (from s in dc.tblWarehouses
                                select s).ToList();
                return Json(new SelectList(mainList.ToArray(), "id", "Name"), JsonRequestBehavior.AllowGet);
            }
            else if (id_ == 7)
            {
                var mainList = (from s in dc.tblWholesellers
                                select s).ToList();
                return Json(new SelectList(mainList.ToArray(), "id", "Name"), JsonRequestBehavior.AllowGet);
            }
            else if (id_ == 8)
            {
                //var mainList = (from s in dc.
                //                select s).ToList();
                //return Json(new SelectList(mainList.ToArray(), "id", "itemName"), JsonRequestBehavior.AllowGet);
            }


            return null;
        }


    }
}