using ims_mat_pro.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Linq.Dynamic;
using System.Collections;

namespace ims_mat_pro.Controllers
{
    public class subInventoryViewController : Controller
    {
        int sessionIssueId;
       
        [HttpGet]
        public ActionResult Index()
        {
          


            return View();
        }
        public ActionResult Add(string itemCode, string pacA, string pacB, string small, string medium, string large, string xlarge)
        {
            try
            {
               
                using (linqDBContext db = new linqDBContext())
                {
                    int pacA_ = (Convert.ToInt32(pacA));
                    int pacB_ = (Convert.ToInt32(pacB));
                    int small_ = Convert.ToInt32(small);
                    int medium_ = Convert.ToInt32(medium);
                    int large_ = Convert.ToInt32(large);
                    int xlarge_ = Convert.ToInt32(xlarge);

                    int s = 0, m = 0, l = 0, xl = 0;
                    int pacA_H = 0, pacB_H = 0, S_H = 0, M_H = 0, L_H = 0,XL_H = 0;

                    //tblIssue - ID
                    int issueID = Convert.ToInt32(Session["sessionIssueID"]);

                    var itemData = (from us in db.tblItems
                                    where us.itemCode.Contains(itemCode)
                                    select us).FirstOrDefault();
                    if (itemData != null)
                    {
                        var issueData = (from us in db.tblIssues
                                         where us.id == issueID
                                         select us).FirstOrDefault();
                       
                        //Previously issued - Total
                        try
                        {
                            var pach = (from us in db.tblsubInventoryDetails
                                        where us.issueId == issueID
                                        select us.packetA.Value).Sum();
                            if (pach > 0)
                            {
                                pacA_H = pach;
                            }
                        }
                        catch (Exception x)
                        { }

                        try
                        {
                            var pach = (from us in db.tblsubInventoryDetails
                                        where us.issueId == issueID
                                        select us.packetB.Value).Sum();
                            if (pach > 0)
                            {
                                pacB_H = pach;
                            }
                        }
                        catch (Exception x)
                        { }

                        try
                        {
                            var pach = (from us in db.tblsubInventoryDetails
                                        where us.issueId == issueID
                                        select us.small.Value).Sum();
                            if (pach > 0)
                            {
                                S_H = pach;
                            }
                        }
                        catch (Exception x)
                        { }

                        try
                        {
                            var pach = (from us in db.tblsubInventoryDetails
                                        where us.issueId == issueID
                                        select us.medium.Value).Sum();
                            if (pach > 0)
                            {
                                M_H = pach;
                            }
                        }
                        catch (Exception x)
                        { }

                        try
                        {
                            var pach = (from us in db.tblsubInventoryDetails
                                        where us.issueId == issueID
                                        select us.large.Value).Sum();
                            if (pach > 0)
                            {
                                L_H = pach;
                            }
                        }
                        catch (Exception x)
                        { }

                        try
                        {
                            var pach = (from us in db.tblsubInventoryDetails
                                        where us.issueId == issueID
                                        select us.xLarge.Value).Sum();
                            if (pach > 0)
                            {
                                XL_H = pach;
                            }
                        }
                        catch (Exception x)
                        { }

                        if ((pacA_ + pacA_H) > issueData.packetA)
                        {
                            throw new Exception("Cannot issue more than " + (issueData.packetA - pacA_H) + " A packets.");
                        }
                        if ((pacB_ + pacB_H) > issueData.packetB)
                        {
                            throw new Exception("Cannot issue more than " + (issueData.packetB - pacB_H) + " B packets.");
                        }
                        if ((small_ + S_H) > issueData.small)
                        {
                            throw new Exception("Cannot issue more than " + (issueData.small - S_H) + " S items.");
                        }
                        if ((medium_ + M_H) > issueData.medium)
                        {
                            throw new Exception("Cannot issue more than " + (issueData.medium - M_H) + " M items.");
                        }
                        if ((large_ + L_H) > issueData.large)
                        {
                            throw new Exception("Cannot issue more than " + (issueData.large - L_H) + " L items.");
                        }
                        if ((xlarge_ + XL_H) > issueData.xLarge)
                        {
                            throw new Exception("Cannot issue more than " + (issueData.xLarge - XL_H) + " XL items.");
                        }

                        //int assignedID = 0;
                        var issueTable = issueData;

                        tblsubInventoryDetail siDetail;
                        tblSubInventory si;

                        var subDetails = (from a in db.tblsubInventoryDetails
                                             where a.issueId == issueData.id
                                             && a.itemId == itemData.id
                                             select a).FirstOrDefault();
                        if (subDetails != null) //Edit
                        {
                            subDetails.packetA += pacA_;
                            subDetails.packetB += pacB_;
                            subDetails.small += small_;
                            subDetails.medium += medium_;
                            subDetails.large += large_;
                            subDetails.xLarge += xlarge_;
                            db.SaveChanges();

                            //Inv
                            var invData = (from a in db.tblSubInventories
                                             where a.itemId == itemData.id
                                             && a.networkID == issueData.networkID
                                             select a).FirstOrDefault();
                            if (invData != null)
                            {
                                invData.small += (Program.small * (pacA_)) + (Program.small * (pacB_)) + small_;
                                invData.medium += (Program.medium * (pacA_)) + (Program.medium * (pacB_)) + medium_;
                                invData.large += (Program.large * (pacA_)) + (Program.large * (pacB_)) + large_;
                                invData.xLarge += (Program.xLarge * (pacA_)) + (Program.xLarge * (pacB_)) + xlarge_;
                                db.SaveChanges();
                            }
                            else //New inv
                            {
                                tblSubInventory inv = new tblSubInventory();
                                inv.small = (Program.small * (pacA_)) + (Program.small * (pacB_)) + small_;
                                inv.medium = (Program.medium * (pacA_)) + (Program.medium * (pacB_)) + medium_;
                                inv.large = (Program.large * (pacA_)) + (Program.large * (pacB_)) + large_;
                                inv.xLarge = (Program.xLarge * (pacA_)) + (Program.xLarge * (pacB_)) + xlarge_;
                                inv.itemId = itemData.id;
                                inv.networkID = issueData.networkID;
                                inv.issueRetailId = issueData.issuedRetailId;
                                db.tblSubInventories.Add(inv);
                                db.SaveChanges();
                            }
                            
                        }
                        else //Add
                        {
                            tblsubInventoryDetail sd = new tblsubInventoryDetail();
                            sd.packetA = pacA_;
                            sd.packetB = pacB_;
                            sd.small = small_;
                            sd.medium = medium_;
                            sd.large = large_;
                            sd.xLarge = xlarge_;
                            sd.itemId = itemData.id;
                            sd.issueId = issueData.id;
                            db.tblsubInventoryDetails.Add(sd);
                            db.SaveChanges();

                            var invCheck = (from us in db.tblSubInventories
                                            where us.itemId == itemData.id
                                            & us.networkID == 3
                                            select us).FirstOrDefault();
                            if (invCheck != null) //Update existing
                            {
                                invCheck.small += (Program.small * (pacA_)) + (Program.small * (pacB_)) + small_;
                                invCheck.medium += (Program.medium * (pacA_)) + (Program.medium * (pacB_)) + medium_;
                                invCheck.large += (Program.large * (pacA_)) + (Program.large * (pacB_)) + large_;
                                invCheck.xLarge += (Program.xLarge * (pacA_)) + (Program.xLarge * (pacB_)) + xlarge_;
                                db.SaveChanges();
                            }
                            else //New inv
                            {
                                tblSubInventory inv = new tblSubInventory();
                                inv.small = (Program.small * (pacA_)) + (Program.small * (pacB_)) + small_;
                                inv.medium = (Program.medium * (pacA_)) + (Program.medium * (pacB_)) + medium_;
                                inv.large = (Program.large * (pacA_)) + (Program.large * (pacB_)) + large_;
                                inv.xLarge = (Program.xLarge * (pacA_)) + (Program.xLarge * (pacB_)) + xlarge_;
                                inv.itemId = itemData.id;
                                inv.networkID = issueData.networkID;
                                inv.issueRetailId = issueData.issuedRetailId;
                                db.tblSubInventories.Add(inv);
                                db.SaveChanges();
                            }
                        }

                        
                    }
                    else
                    {
                        throw new Exception("Invalid product code.");
                    }
                }

                return Json(new JsonResult()
                {
                    Data = "success"
                }, JsonRequestBehavior.AllowGet);

            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        //public ActionResult Edit(int id, string Type)
        //{
        //    try
        //    {
        //        using (linqDBContext db = new linqDBContext())
        //        {
        //            var query = (from us in db.tblSubInventories
        //                         where us.id == id
        //                         select us).FirstOrDefault();
        //            if (query != null)
        //            {
        //                query. = Type;
        //                db.SaveChanges();
        //
        //                return Json(new JsonResult()
        //                {
        //                    Data = "Success"
        //                }, JsonRequestBehavior.AllowGet);
        //            }
        //            else
        //            {
        //                return Json(new JsonResult()
        //                {
        //                    Data = "Not found!"
        //                }, JsonRequestBehavior.AllowGet);
        //            }
        //        }
        //
        //
        //    }
        //    catch (Exception x)
        //    {
        //        return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
        //    }
        //}


        public ActionResult Populate(string id)
        {

            try
            {
                using (linqDBContext db = new linqDBContext())
                {
                    int lotid =Convert.ToInt32(Session["sessionlotid"]);
                    var proID = db.tblItems.Where(x => x.itemCode.Contains(id)).Select(y => y.id).FirstOrDefault();

                    var query = (from us in db.tblItems 
                                 join l in db.tblLots  on us.lotId equals l.id                                
                                 join m in db.tblMainCategories on us.mainCategory equals m.id
                                 where us.lotId == lotid && us.id == proID
                                 select new { id = us.id, lotname = l.lotName,itm=us.itemName,netPrice=us.netPrice,itmCode=us.itemCode,category=m.name }).FirstOrDefault();
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
                            Data = "failed"
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
                    //Remove from inv
                    var data = db.tblsubInventoryDetails.Single(x => x.id == id);                   
                    if (data != null)
                    {
                        int small = (Program.small * (data.packetA.Value)) + (Program.small * (data.packetB.Value)) + data.small.Value;
                        int medium = (Program.medium * (data.packetA.Value)) + (Program.medium * (data.packetB.Value)) + data.medium.Value;
                        int large = (Program.large * (data.packetA.Value)) + (Program.large * (data.packetB.Value)) + data.large.Value;
                        int xL = (Program.xLarge * (data.packetA.Value)) + (Program.xLarge * (data.packetB.Value)) + data.xLarge.Value;

                        
                        var invData = (from a in db.tblSubInventories
                                       where a.networkID == 3 //Resellers ID
                                       && a.itemId == data.itemId
                                       select a).FirstOrDefault();
                        if (invData != null)
                        {
                            if (invData.small >= small)
                            {
                                invData.small -= small;
                            }
                            else
                            {
                                return Json(new { status = "error", Data = "S item has only " + invData.small.Value + " qty. It cannot be reverted!." }, JsonRequestBehavior.AllowGet);
                            }
                            if (invData.medium >= medium)
                            {
                                invData.medium -= medium;
                            }
                            else
                            {
                                return Json(new { status = "error", Data = "M item has only " + invData.medium.Value + " qty. It cannot be reverted!." }, JsonRequestBehavior.AllowGet);
                            }
                            if (invData.large >= large)
                            {
                                invData.large -= large;
                            }
                            else
                            {
                                return Json(new { status = "error", Data = "L item has only " + invData.large.Value + " qty. It cannot be reverted!." }, JsonRequestBehavior.AllowGet);
                            }
                            if (invData.xLarge >= xL)
                            {
                                invData.xLarge -= xL;
                            }
                            else
                            {
                                return Json(new { status = "error", Data = "XL item has only " + invData.xLarge.Value + " qty. It cannot be reverted!." }, JsonRequestBehavior.AllowGet);
                            }
                            db.SaveChanges();
                            db.tblsubInventoryDetails.Remove(data);
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
                    int sessionIssueId = Convert.ToInt32(Session["sessionIssueID"]);

                    var v = (from a in abc.tblsubInventoryDetails where a.issueId == sessionIssueId
                             join d in abc.tblIssues on a.issueId equals d.id
                             join c in abc.tblItems on a.itemId equals c.id 


                             select new { id = a.id, itemCode = c.itemCode, pacA=a.packetA,pacB=a.packetB, small = a.small, med = a.medium, large = a.large,xlarge=a.xLarge });




                    if (!(string.IsNullOrEmpty(sortColumn) && string.IsNullOrEmpty(sortColumnDir)))
                    {
                        v = v.OrderBy(sortColumn + " " + sortColumnDir);
                    }

                    //Search   
                    var searchValue = Request.Form.GetValues("search[value]").FirstOrDefault();
                    if (!string.IsNullOrEmpty(searchValue))
                    {
                        v = v.Where(m => m.itemCode.Contains(searchValue));
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

        //
        public JsonResult checkitm(string id)
        {
            try
            {


                using (linqDBContext db = new linqDBContext())
                {
                    //sessionIssueId = Convert.ToInt32(Session["sessionIssueID"]);
                    int lotID = Convert.ToInt32(Session["sessionlotid"]);  

                    var itmid = (from a in db.tblItems
                                where a.itemCode.Contains(id)
                                && a.lotId == lotID
                                 select a).FirstOrDefault();
                    if (itmid == null)
                    {
                        return Json(new JsonResult()
                        {
                            Data = 0
                        }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    {
                        return Json(new JsonResult()
                        {
                            Data = itmid.itemCode
                        }, JsonRequestBehavior.AllowGet);
                    }

                    //var chk = (from a in db.tblsubInventoryDetails where a.issueId == sessionIssueId && a.itemId == itmid.id select a).FirstOrDefault();

                    //if (chk==null)
                    //{
                    //    return Json(new JsonResult()
                    //    {
                    //        Data = 0
                    //    }, JsonRequestBehavior.AllowGet);
                    //}
                    //return Json(new JsonResult()
                    //{
                    //    Data = itmid.itemCode
                    //}, JsonRequestBehavior.AllowGet);
                }
            }
            catch (Exception x)
            {

                return Json(new { status = "error", Data = 0 }, JsonRequestBehavior.AllowGet);
            }

            

        }
        
    }
}