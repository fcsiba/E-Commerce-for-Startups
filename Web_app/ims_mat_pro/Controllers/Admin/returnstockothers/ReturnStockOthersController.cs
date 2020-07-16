using ims_mat_pro.Models;
using System;
using System.Linq;
using System.Web.Mvc;
using System.Linq.Dynamic;
using System.Collections.Generic;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System.Collections;
using System.Data.Entity;

namespace ims_mat_pro.Controllers.Admin.ReturnStock
{
    public class ReturnStockOthersController : Controller
    {
        linqDBContext dc = new linqDBContext();
               // GET: ReturnStockOthers
        public ActionResult Index()
        {
            int utype = Convert.ToInt32(Session["uType"]);

            //if (utype == 1 )
            //{

            //    return (RedirectToAction("Index", "ReturnStockWholeseller"));
            //}
            //else 
            //{
                #region
                var T = (from x in dc.tblRetails
                         where x.id != 6 && x.id != 7 && x.id != 4
                         select x).ToList();

                List<SelectListItem> typelist = new List<SelectListItem>();

                foreach (var itemm in T)
                {
                    typelist.Add(new SelectListItem
                    {
                        Text = itemm.name,
                        Value = itemm.id.ToString()
                    });
                }



                ViewBag.type = typelist;
                #endregion
                return View();
            

          
        }
        //load data
        public ActionResult LoadData()
        {
            int local, type;
            if (TempData.Count < 2)
            {
                return Json(new JsonResult()
                {
                    Data = "nodata"
                }, JsonRequestBehavior.AllowGet);

            }
            else
            {
                 local = Convert.ToInt32(TempData.Peek("local"));
                 type = Convert.ToInt32(TempData.Peek("type"));
                TempData.Keep("local");
                TempData.Keep("type");


            }

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



                //int sessionIssueId = Convert.ToInt32(Session["sessionIssueID"]);

                  var v = (from a in dc.tblSubInventories
                             join c in dc.tblItems on a.itemId equals c.id
                             join d in dc.issuedRetailers on a.issueRetailId equals d.Id where  d.localId == local &&  d.retailId == type
                             


                             select new { id = a.id, itemName = c.itemName, small = a.small, medium = a.medium, large = a.large, xlarge = a.xLarge });


               // TempData["returnList"]  = v;
                

                    if (!(string.IsNullOrEmpty(sortColumn) && string.IsNullOrEmpty(sortColumnDir)))
                    {
                        v = v.OrderBy(sortColumn + " " + sortColumnDir);
                    }

                    //Search   
                    var searchValue = Request.Form.GetValues("search[value]").FirstOrDefault();
                    if (!string.IsNullOrEmpty(searchValue))
                    {
                        v = v.Where(m => m.itemName.Contains(searchValue) ||
                                    m.id.ToString() == searchValue);
                    }


                    recordsTotal = v.Count();
                    var data = v.Skip(skip).Take(pageSize).ToList();
                    var dt = data;
                    return Json(new { draw = draw, recordsFiltered = recordsTotal, recordsTotal = recordsTotal, data = data });
                


            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }

        }
        public JsonResult itemList(string Id)
        {

            int id_ = Convert.ToInt32(Id);

            if (id_ == 1)
            {

                //item = (from s in dc.tbl
                //             where s.lotId == id_
                //             select s).ToList();
                return null;
            }
            else if (id_ == 2)
            {
                //item = (from s in dc.tbl
                //           where s.lotId == id_
                //          select s).ToList();
                return null;

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

         

                return null;
            
        }

        public ActionResult asignIds(string typeid,string retailid)
        {
            try
            {



                int localid_ = Convert.ToInt32(retailid);
                int retailid_ = Convert.ToInt32(typeid);

                if (localid_ <= 0 && retailid_ <= 0)
                {
                    TempData.Add("type", 0);
                    TempData.Add("local", 0);
                    TempData.Keep("type");
                    TempData.Keep("local");
                    return Json(new JsonResult()
                    {
                        Data = "nodata"
                    }, JsonRequestBehavior.AllowGet);

                }
                else
                {
                    TempData.Remove("type");
                    TempData.Remove("local");
                    
                    // ViewBag.localid = localid_;
                    TempData.Add("type", retailid_);
                    TempData.Add("local", localid_);
                    TempData.Keep("type");
                    TempData.Keep("local");
                    // ViewBag.retailid = retailid_;
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
        //add
        public ActionResult Add(string reason,string bit)
        {
            try
            {
                int localid_ = Convert.ToInt32(TempData.Peek("local"));
                int retailid_ = Convert.ToInt32(TempData.Peek("type"));

                // ViewBag.localid = localid_;
                if (localid_==0||retailid_==0)
                {
                    throw new Exception("No items In Table To Return");
                }
                using (linqDBContext db = new linqDBContext())
                {
                    var v = (from a in db.tblSubInventories
                             join c in db.tblItems on a.itemId equals c.id
                             join d in db.issuedRetailers on a.issueRetailId equals d.Id
                             where d.localId == localid_ && d.retailId == retailid_



                             select new {id=a.id, issuedid = a.issueRetailId, itmid = c.id, small = a.small, medium = a.medium, large = a.large, xlarge = a.xLarge });
                    int firstTime = 0;
                    int counter = 0;

                    foreach (var item in v)
                    {
                        tblSubInventory tsi= (from a in dc.tblSubInventories
                                              where a.itemId == item.itmid && a.issueRetailId ==item.issuedid
                                              select a).FirstOrDefault();


                        tblreturnHistoryDetail trd = new tblreturnHistoryDetail();
                        if (firstTime==0)
                        {
                                var chk= (from a in dc.tblreturnHistoryDetails select a).ToList();
                            if (chk.Count==0)
                            {
                                counter = 0;


                            }
                            else
                            {
                                counter = chk.Last().RID.Value;
                            }

                            counter++;
                            firstTime = 1;
                        }
                        trd.RID = counter;
                      
                        trd.issueRetailId = item.issuedid;
                        trd.itmId = item.itmid;
                        trd.small = item.small;
                        trd.medium = item.medium;
                        trd.large = item.large;
                        trd.xlarge = item.xlarge;
                        trd.date = DateTimeOffset.Now.Date.ToShortDateString();
                        dc.tblreturnHistoryDetails.Add(trd);
                        dc.SaveChanges();

                        tsi.small -= item.small;
                        tsi.medium -= item.medium;
                        tsi.large -= item.large;
                        tsi.xLarge -= item.xlarge;

                        // dc.tblSubInventories.Remove(tsi);
                        dc.SaveChanges();

                    }
                    // now group the ReturnHistorydetail data Category wise and save it ito Returnhistory Table
                    tblReturnHistory Rh;
                    var xx = (from a in dc.tblreturnHistoryDetails
                              where a.RID == counter
                              join b in dc.tblItems on a.itmId equals b.id
                              select new {isu=a.issueRetailId,cat=b.mainCategory,small=a.small,med=a.medium ,large=a.large,xlarge=a.xlarge,reason=a.reason}).GroupBy(x=>x.cat);

                    
                    foreach (var item in xx)
                    {
                        int getcat = item.First().cat.Value;
                        Rh = new tblReturnHistory();
                        int S = 0, M = 0, L = 0, XL = 0;
                        foreach (var list in item)
                        {


                            S += (int)list.small;
                            M += (int)list.med;
                            L += (int)list.large;
                            XL += (int)list.xlarge;

                            Rh.issueRetailId = list.isu;
                          }
                       
                        Rh.small = S;
                        Rh.medium = M;
                        Rh.large = L;
                        Rh.xLarge = XL;
                        Rh.category_Id = getcat;
                        Rh.date = DateTimeOffset.Now.Date;
                        if (bit.Equals("True"))
                        {  // if checkbox is cheked he want to return to main inventory then...
                            Rh.save_to_main_inventory = true;
                           // tblMainInventry tmi;
                           var tmi = (from a in dc.tblMainInventries where a.mainCatId == getcat select a).FirstOrDefault();
                            if (tmi != null)
                            {
                                tmi.small += S;
                                tmi.medium += M;
                                tmi.large += L;
                                tmi.xLarge += XL;
                                db.Entry(tmi).State = EntityState.Modified;
                                db.SaveChanges();

                            }
                            else
                            {
                                tmi = new tblMainInventry();
                                tmi.mainCatId = getcat;
                                tmi.small = S;
                                tmi.medium = M;
                                tmi.large = L;
                                tmi.xLarge = XL;
                                db.tblMainInventries.Add(tmi);
                                db.SaveChanges();

                            }

                        }
                        else
                        {
                            //if checkbox is uncheked then items will save into return inventory
                            Rh.save_to_main_inventory = false;
                            //tblReturnInventory tri;
                            var tri = (from a in dc.tblReturnInventories where a.mainCatId == getcat select a).FirstOrDefault();
                            if (tri!=null)
                            {
                                tri.small += S;
                                tri.medium += M;
                                tri.large += L;
                                tri.xLarge += XL;
                          
                            }
                            else
                            {
                                tri = new tblReturnInventory();
                                tri.mainCatId = getcat;
                                tri.small = S;
                                tri.medium = M;
                                tri.large = L;
                                tri.xLarge = XL;
                                db.tblReturnInventories.Add(tri);

                            }
                       



                        }
                        db.tblReturnHistories.Add(Rh);
                        db.SaveChanges();


                    }






                    return Json(new JsonResult()
                    {
                        Data = "Success"
                    }, JsonRequestBehavior.AllowGet);
                }
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }
    

    }
}