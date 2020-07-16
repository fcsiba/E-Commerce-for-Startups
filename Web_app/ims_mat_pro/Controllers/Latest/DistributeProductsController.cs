using ims_mat_pro.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Linq.Dynamic;

namespace ims_mat_pro.Controllers.Latest
{
    public class DistributeProductsController : Controller
    {
        // GET: DistributeProducts
        public ActionResult Index()
        {
            return View();
        }

        public JsonResult GetItem(string id)
        {
            try
            {


                using (linqDBContext db = new linqDBContext())
                {
                    //sessionIssueId = Convert.ToInt32(Session["sessionIssueID"]);
                    int lotID = Convert.ToInt32(Session["lotID"]);
                    var item = db.tblItems.Where(x => x.itemCode.Contains(id) && x.lotId == lotID).FirstOrDefault();
                   
                    if (item == null)
                    {
                        return Json(new { status = "error", Data = "This item does not belong to the selected lot." }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    {
                        return Json(new { status = "success", Data = item }, JsonRequestBehavior.AllowGet);
                    }
                   
                }
            }
            catch (Exception x)
            {

                return Json(new { status = "error", Data = 0 }, JsonRequestBehavior.AllowGet);
            }



        }

        public ActionResult Distribute(string itemCode, int pacA, int pacB, int[] szArr, int[] vals, int count)
        {
            try
            {
                string[] sizes = new string[50];
                int issuedID = Convert.ToInt32(Session["lid"]);
                using (linqDBContext db = new linqDBContext())
                {                   
                    var lot_issued = db.Lot_Issued.Where(x => x.id == issuedID).FirstOrDefault(); //Pac A - B
                    var lot_issued_loose = db.Lot_Issued_Loose.Where(x => x.primaryID == issuedID).ToList(); //Loose stock                    
                    var lot = db.Lots.Where(x => x.id == lot_issued.lotID).FirstOrDefault(); //Lot

                    var itemData = (from us in db.tblItems
                                    where us.itemCode.Contains(itemCode)
                                    select us).FirstOrDefault();
                    if (itemData != null)
                    {
                        for (int i = 0; i < count; i++) //Map sizes from Lot_Issued_Loose
                        {
                            int sID = szArr[i];
                            var sz = db.Lot_Loose_Items.Where(x => x.id == sID).Select(y => y.size).FirstOrDefault();
                            sizes[i] = sz;
                        }

                        #region Pack_issued_Total

                        int pacA_already_issued = 0, pacB_already_issued = 0;
                        try //Pack - Previous Total
                        {
                            var pach = (from us in db.Lot_Distributed
                                        where us.issueId == issuedID
                                        select us.pacA.Value).Sum();
                            if (pach > 0)
                            {
                                pacA_already_issued = pach;
                            }
                        }
                        catch (Exception x)
                        { }

                        try
                        {
                            var pach = (from us in db.Lot_Distributed
                                        where us.issueId == issuedID
                                        select us.pacB.Value).Sum();
                            if (pach > 0)
                            {
                                pacB_already_issued = pach;
                            }
                        }
                        catch (Exception x)
                        { }

                        #endregion

                        #region Validation

                        if ((pacA + pacA_already_issued) > lot_issued.packet_A)
                        {
                            throw new Exception("Cannot issue more than " + (lot_issued.packet_A - pacA_already_issued) + " A packets.");
                        }
                        if ((pacB + pacB_already_issued) > lot_issued.packet_B)
                        {
                            throw new Exception("Cannot issue more than " + (lot_issued.packet_B - pacB_already_issued) + " B packets.");
                        }

                        #region Check-Loose-Stock

                        for (int i = 0; i < count; i++)
                        {
                            string size = sizes[i];
                            int issuedTotal = 0;

                            try //Previous Total
                            {
                                var issued = (from us in db.Lot_Distributed_Loose
                                              where us.issueId == issuedID                                              
                                              && us.size == size
                                              select us.qty).Sum();
                                if (issued > 0)
                                {
                                    issuedTotal = issued.Value;
                                }
                            }
                            catch (Exception x)
                            { }

                            var lot_issued_loose_current = lot_issued_loose.Where(x => x.size == size).FirstOrDefault();

                            if ((vals[i] + issuedTotal) > lot_issued_loose_current.qty)
                            {
                                throw new Exception("Cannot issue more than " + (lot_issued_loose_current.qty - issuedTotal) + " in size " + size + ".");
                            }
                        }

                        #endregion

                        #endregion

                        #region Packs
                        //Packs
                        var lot_distributed = (from us in db.Lot_Distributed
                                               where us.issueId == issuedID
                                               && us.itemId == itemData.id
                                               select us).FirstOrDefault();
                        if (lot_distributed != null) //Edit
                        {
                            lot_distributed.pacA += pacA;
                            lot_distributed.pacB += pacB;                           
                        } 
                        else //Add
                        {
                            Lot_Distributed sd = new Lot_Distributed();
                            sd.issueId = issuedID;
                            sd.itemId = itemData.id;
                            sd.pacA = pacA;
                            sd.pacB = pacB;
                            sd.date = DateTime.Now;
                            db.Lot_Distributed.Add(sd);
                        }


                        //Inv for A & B
                        List<Packets_Scale> sizesQuery = Helper.Get_Pack_Qty(lot.mainCatId.Value);
                        if (sizesQuery != null)
                        {
                            foreach (var item in sizesQuery) //Packet_Scale_Ratio
                            {
                                var invData = (from us in db.Inventory_Latest
                                               where us.itemId == itemData.id
                                               && us.network == lot_issued.networkID
                                               && us.localID == lot_issued.localID
                                               && us.size == item.size
                                               select us).FirstOrDefault();
                                if (invData != null)
                                {
                                    invData.qty += (item.qty * pacA) + (item.qty * pacB); //Break pack A & B into pieces                               
                                }
                                else //New inv
                                {
                                    Inventory_Latest inv = new Inventory_Latest();
                                    inv.network = lot_issued.networkID;
                                    inv.localID = lot_issued.localID;
                                    inv.itemId = itemData.id;
                                    inv.size = item.size;
                                    inv.qty = (item.qty * pacA) + (item.qty * pacB); //Break pack A & B into pieces     
                                    db.Inventory_Latest.Add(inv);
                                }
                            }
                            db.SaveChanges();
                        }

                        #endregion

                        #region Loose

                        for (int i = 0; i < count; i++)
                        {
                            string size = sizes[i];
                                                       
                            //Distributed record
                            var lot_distributed_loose = (from us in db.Lot_Distributed_Loose
                                                         where us.issueId == issuedID
                                                         && us.itemId == itemData.id
                                                         && us.size == size
                                                         select us).FirstOrDefault();
                            if (lot_distributed_loose != null)
                            {
                                lot_distributed_loose.qty += vals[i];                               
                            }
                            else
                            {
                                Lot_Distributed_Loose sd = new Lot_Distributed_Loose();
                                sd.issueId = issuedID;
                                sd.itemId = itemData.id;
                                sd.size = size;
                                sd.qty = vals[i];                               
                                sd.date = DateTime.Now;
                                db.Lot_Distributed_Loose.Add(sd);
                            }

                            //Inventory
                            var invData = (from us in db.Inventory_Latest
                                           where us.itemId == itemData.id
                                           && us.network == lot_issued.networkID
                                           && us.localID == lot_issued.localID
                                           && us.size == size
                                           select us).FirstOrDefault();
                            if (invData != null)
                            {
                                invData.qty += vals[i];                          
                            }
                            else //New inv
                            {
                                Inventory_Latest inv = new Inventory_Latest();
                                inv.network = lot_issued.networkID;
                                inv.localID = lot_issued.localID;
                                inv.itemId = itemData.id;
                                inv.size = size;
                                inv.qty = vals[i];
                                db.Inventory_Latest.Add(inv);
                            }

                        }
                       
                        #endregion

                        db.SaveChanges();

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

        [HttpPost]
        public ActionResult LoadData()
        {
            try
            {
                Program.createTable("drop table temp_dist");
                Program.createTable("DECLARE @cols AS NVARCHAR(MAX), @query AS NVARCHAR(MAX) select @cols = STUFF((SELECT ',' + QUOTENAME(size) from Lot_Distributed_Loose group by size order by size FOR XML PATH(''), TYPE ).value('.', 'NVARCHAR(MAX)') ,1,1,'') set @query = 'SELECT id = IDENTITY(INT, 1, 1), issueId, itemId,' + @cols + ' into temp_dist from ( select issueId, itemId, size, qty from Lot_Distributed_Loose) x pivot ( sum(qty) for size in (' + @cols + ') ) p ' execute(@query);");
                Program.createTable("ALTER TABLE temp_dist ADD PRIMARY KEY ( id );");


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
                    int issueID = Convert.ToInt32(Session["lid"]);

                    var v = (from a in abc.vDists
                             where a.issueId == issueID                         
                             orderby a.itemId
                             select new { id = a.itemId, Code = a.itemCode, PacA = a.pacA, PacB = a.pacB, S = a.S, M = a.M, L = a.L, XL = a.XL, a.XXL, a.XXXL, K_14 = a.K_14, K_16 = a.K_16, K_18 = a.K_18, K_20 = a.K_20, K_22 = a.K_22, K_24 = a.K_24, K_26 = a.K_26, K_28 = a.K_28, _28 = a.C28, _30 = a.C30, _32 = a.C32, _34 = a.C34, _36 = a.C36, _38 = a.C38, _40 = a.C40, _42 = a.C42, _44 = a.C44 });
                    
                  
                    if (!(string.IsNullOrEmpty(sortColumn) && string.IsNullOrEmpty(sortColumnDir)))
                    {
                        v = v.OrderBy(sortColumn + " " + sortColumnDir);
                    }

                    //Search   
                    var searchValue = Request.Form.GetValues("search[value]").FirstOrDefault();
                    if (!string.IsNullOrEmpty(searchValue))
                    {
                        v = v.Where(m => m.Code.Contains(searchValue));
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