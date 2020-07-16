using ims_mat_pro.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Linq.Dynamic;

namespace ims_mat_pro.Controllers.Latest
{
    public class LotIssuedController : Controller
    {
        linqDBContext dc = new linqDBContext();
        
        // GET: LotIssued
        public ActionResult Index()
        {
            var t = (from x in dc.tblRetails
                     where x.id != 1 && x.id != 2
                     select x).ToList();

            var L = (from x in dc.Lots
                     select x).ToList();


            List<SelectListItem> retailList = new List<SelectListItem>();
            List<SelectListItem> lotList = new List<SelectListItem>();

            foreach (var itemm in t)
            {
                retailList.Add(new SelectListItem
                {
                    Text = itemm.name,
                    Value = itemm.id.ToString()
                });
            }
            foreach (var itemm in L)
            {
                lotList.Add(new SelectListItem
                {
                    Text = itemm.lotName,
                    Value = itemm.id.ToString()
                });
            }


            ViewBag.retailList = retailList;
            ViewBag.lotList = lotList;


            return View();
        }

        public ActionResult Issue(int editID , int networkID, int local, int lot, int pacA, int pacB, int[] szArr, int[] vals, int count)
        {            
            try
            {
                string[] sizes = new string[50];         
                using (linqDBContext db = new linqDBContext())
                {
                   

                    #region Add

                    if (editID == 0)
                    {
                        var mainCatID = db.Lots.Where(x => x.id == lot).Select(y => y.mainCatId.Value).FirstOrDefault();
                        var lot_Data = db.Lots.Where(x => x.id == lot).FirstOrDefault();
                        int issuedSum_A = 0, issuedSum_B = 0;

                        for (int i = 0; i < count; i++) //Map sizes from Lot_Loose_Items
                        {
                            int sID = szArr[i];
                            var sz = db.Lot_Loose_Items.Where(x => x.id == sID).Select(y => y.size).FirstOrDefault();
                            sizes[i] = sz;
                        }

                        #region Previous-Sum

                        try
                        {
                            var tA = (from us in db.Lot_Issued
                                      where us.lotID == lot
                                      select us.packet_A.Value).Sum();
                            if (tA > 0)
                            {
                                issuedSum_A = tA;
                            }
                        }
                        catch (Exception xz)
                        { }

                        try
                        {
                            var tB = (from us in db.Lot_Issued
                                      where us.lotID == lot
                                      select us.packet_B.Value).Sum();
                            if (tB > 0)
                            {
                                issuedSum_B = tB;
                            }
                        }
                        catch (Exception xz)
                        { }

                        #endregion

                        if (pacA > (lot_Data.A - issuedSum_A))
                        {
                            return Json(new { status = "error", Data = "Cannot add more than " + (lot_Data.A - issuedSum_A) + " A packets." }, JsonRequestBehavior.AllowGet);
                        }
                        else if (pacB > (lot_Data.B - issuedSum_B))
                        {
                            return Json(new { status = "error", Data = "Cannot add more than " + (lot_Data.B - issuedSum_B) + " B packets." }, JsonRequestBehavior.AllowGet);
                        }

                        #region Check-Loose-Stock

                        for (int i = 0; i < count; i++)
                        {
                            string size = sizes[i];
                            int issuedTotal = 0;

                            try //Previous Total
                            {
                                var issued = (from us in db.Lot_Issued_Loose
                                          where us.lotID == lot
                                          && us.size == size
                                          select us.qty.Value).Sum();
                                if (issued > 0)
                                {
                                    issuedTotal = issued;
                                }
                            }
                            catch (Exception x)
                            {}

                            var lot_loose_item = db.Lot_Loose_Items.Where(x => x.lotID == lot && x.size == size).Select(y => y.qty).FirstOrDefault(); //Qty in issued lot (loose stock)

                            if (vals[i] > (lot_loose_item - issuedTotal))
                            {
                                return Json(new { status = "error", Data = "Cannot add more than " + (lot_loose_item - issuedTotal) + " in size " + size + "." }, JsonRequestBehavior.AllowGet);
                            }
                        }

                        #endregion

                        //Packs
                        Lot_Issued a = new Lot_Issued();
                        a.lotID = lot;
                        a.networkID = networkID;
                        a.localID = local;
                        a.packet_A = pacA;
                        a.packet_B = pacB;
                        a.date = DateTime.Now;
                        db.Lot_Issued.Add(a);
                        db.SaveChanges();

                        //Update main inventory
                        var mainInv = (from b in db.Main_Inventory
                                       where b.mainCatID == mainCatID
                                       select b).FirstOrDefault();
                        if (mainInv != null)
                        {
                            mainInv.packet_A -= pacA;
                            mainInv.packet_B -= pacB;
                        }

                        //Loose-Stock
                        for (int i = 0; i < count; i++)
                        {
                            string size = sizes[i];
                            Lot_Issued_Loose l = new Lot_Issued_Loose();
                            l.primaryID = a.id;
                            l.lotID = lot;
                            l.size = size;
                            l.qty = vals[i];
                            l.date = DateTime.Now;
                            db.Lot_Issued_Loose.Add(l);

                            //Loose Inventory                           
                            var looseInv = (from us in db.Main_Inventory_Loose
                                            where us.mainCatID == mainCatID
                                            && us.size == size
                                            select us).FirstOrDefault();
                            if (looseInv != null)
                            {
                                looseInv.qty -= vals[i];
                            }
                        }

                       

                        db.SaveChanges();
                    }

                    #endregion

                    #region Edit
                    else
                    {
                        for (int i = 0; i < count; i++) //Map sizes from Lot_Issued_Loose
                        {
                            int sID = szArr[i];
                            var sz = db.Lot_Issued_Loose.Where(x => x.id == sID).Select(y => y.size).FirstOrDefault();
                            sizes[i] = sz;
                        }

                        var lot_issued_Data = (from us in db.Lot_Issued where us.id == editID select us).FirstOrDefault();
                        networkID = lot_issued_Data.networkID.Value;
                        local = lot_issued_Data.localID.Value;
                        lot = lot_issued_Data.lotID.Value;

                        var mainCatID = db.Lots.Where(x => x.id == lot).Select(y => y.mainCatId.Value).FirstOrDefault();
                        var lot_Data = db.Lots.Where(x => x.id == lot).FirstOrDefault();
                        int issuedSum_A = 0, issuedSum_B = 0;

                        if (lot_issued_Data != null)
                        {
                            int dA = pacA - lot_issued_Data.packet_A.Value;
                            int dB = pacB - lot_issued_Data.packet_B.Value;

                            #region Previous-Sum

                            try
                            {
                                var tA = (from us in db.Lot_Issued
                                          where us.lotID == lot
                                          select us.packet_A.Value).Sum();
                                if (tA > 0)
                                {
                                    issuedSum_A = tA;
                                }
                            }
                            catch (Exception xz)
                            { }

                            try
                            {
                                var tB = (from us in db.Lot_Issued
                                          where us.lotID == lot
                                          select us.packet_B.Value).Sum();
                                if (tB > 0)
                                {
                                    issuedSum_B = tB;
                                }
                            }
                            catch (Exception xz)
                            { }

                            #endregion

                            if (dA > (lot_Data.A - issuedSum_A))
                            {
                                return Json(new { status = "error", Data = "Cannot add more than " + (lot_Data.A - issuedSum_A) + " A packets." }, JsonRequestBehavior.AllowGet);
                            }
                            else if (dB > (lot_Data.B - issuedSum_B))
                            {
                                return Json(new { status = "error", Data = "Cannot add more than " + (lot_Data.B - issuedSum_B) + " B packets." }, JsonRequestBehavior.AllowGet);
                            }

                            #region Check-Loose-Stock

                            for (int i = 0; i < count; i++)
                            {
                                string size = sizes[i];
                                int issuedTotal = 0;

                                try //Previous Total
                                {
                                    var issued = (from us in db.Lot_Issued_Loose
                                                  where us.lotID == lot
                                                  && us.size == size
                                                  select us.qty.Value).Sum();
                                    if (issued > 0)
                                    {
                                        issuedTotal = issued;
                                    }
                                }
                                catch (Exception x)
                                { }

                                var lot_loose_item = db.Lot_Loose_Items.Where(x => x.lotID == lot && x.size == size).Select(y => y.qty).FirstOrDefault(); //Qty in issued lot (loose stock)

                                var loose_Data = (from us in db.Lot_Issued_Loose where us.primaryID == editID && us.size == size select us).FirstOrDefault();
                                if (loose_Data != null)
                                {
                                    int diff = vals[i] - loose_Data.qty.Value;

                                    if (diff > (lot_loose_item - issuedTotal)) //Compare with diff
                                    {
                                        return Json(new { status = "error", Data = "Cannot add more than " + (lot_loose_item - issuedTotal + loose_Data.qty) + " in size " + size + "." }, JsonRequestBehavior.AllowGet);
                                    }
                                }


                            }

                            #endregion

                            //Packs
                            lot_issued_Data.packet_A = pacA;
                            lot_issued_Data.packet_B = pacB;

                            //Update main inventory
                            var mainInv = (from b in db.Main_Inventory
                                           where b.mainCatID == mainCatID
                                           select b).FirstOrDefault();
                            if (mainInv != null)
                            {
                                mainInv.packet_A -= dA;
                                mainInv.packet_B -= dB;
                            }

                            //Loose-Stock
                            for (int i = 0; i < count; i++)
                            {
                                string size = sizes[i];
                                var loose_Data = (from us in db.Lot_Issued_Loose where us.primaryID == editID && us.size == size select us).FirstOrDefault();
                                if (loose_Data != null)
                                {
                                    int diff = vals[i] - loose_Data.qty.Value;                                   

                                    //Lot_Issued_Loose
                                    loose_Data.qty = vals[i];


                                    //Loose Inventory                  
                                    var looseInv = (from us in db.Main_Inventory_Loose
                                                    where us.mainCatID == mainCatID
                                                    && us.size == size
                                                    select us).FirstOrDefault();
                                    if (looseInv != null)
                                    {
                                        looseInv.qty -= diff;
                                    }
                                }

                            }

                            db.SaveChanges();
                        }
                    }
                  

                    #endregion

                }
                return Json(new { status = "success", Data = "Done" }, JsonRequestBehavior.AllowGet);



            }

            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }
        //Load DATA
        public ActionResult LoadData()
        {
            Program.createTable("drop table temp_issued");
            Program.createTable("DECLARE @cols AS NVARCHAR(MAX), @query AS NVARCHAR(MAX) select @cols = STUFF((SELECT ',' + QUOTENAME(size) from Lot_Issued_Loose group by size order by size FOR XML PATH(''), TYPE ).value('.', 'NVARCHAR(MAX)') ,1,1,'') set @query = 'SELECT id = IDENTITY(INT, 1, 1), primaryID,' + @cols + ' into temp_issued from ( select primaryID, qty, size from Lot_Issued_Loose ) x pivot ( sum(qty) for size in (' + @cols + ') ) p ' execute(@query);");
            Program.createTable("ALTER TABLE temp_issued ADD PRIMARY KEY ( id );");

            int utype = Convert.ToInt32(Session["uType"]);
            int uId = Convert.ToInt32(Session["uID"]);
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



                    var v = (from a in abc.temp_issued                            
                             join b in abc.Lot_Issued on a.primaryID equals b.id
                             join d in abc.tblRetails on b.networkID equals d.id
                             join l in abc.Lots on b.lotID equals l.id
                             select new { id = b.id, network = d.name, lot = l.lotName, pacA = b.packet_A, pacB = b.packet_B, small = a.S, med = a.M, large = a.L, xlarge = a.XL, xxl = a.XXL, xxxl = a.XXXL });


                    if (!(string.IsNullOrEmpty(sortColumn) && string.IsNullOrEmpty(sortColumnDir)))
                    {
                        v = v.OrderBy(sortColumn + " " + sortColumnDir);
                    }

                    //Search   
                    var searchValue = Request.Form.GetValues("search[value]").FirstOrDefault();
                    if (!string.IsNullOrEmpty(searchValue))
                    {
                        v = v.Where(m => m.lot.Contains(searchValue));
                    }

                    try
                    {
                        if (v != null)
                        {
                            recordsTotal = v.Count();
                            var data = v.Skip(skip).Take(pageSize).ToList();
                            var dt = data;
                            return Json(new { draw = draw, recordsFiltered = recordsTotal, recordsTotal = recordsTotal, data = data });
                        }
                        
                    }
                    catch (Exception x)
                    {
                        recordsTotal = 0;
                        
                    }

                    return Json(new { draw = draw, recordsFiltered = recordsTotal, recordsTotal = recordsTotal, data = "" });

                }


            }
            catch (Exception x)
            {
                throw;
            }

        }

        //populate
        public ActionResult Populate(int id)
        {
            try
            {
                using (linqDBContext db = new linqDBContext())
                {
                    var query = (from us in db.Lot_Issued
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

        //POPULATELOOSE
        public ActionResult PopulateLoose(int id)
        {
            try
            {
                using (linqDBContext db = new linqDBContext())
                {
                    var mainID = db.Lots.Where(x => x.id == id).Select(y => y.mainCatId.Value).FirstOrDefault();

                    var query = (from us in db.Lot_Issued_Loose
                                 where us.primaryID == id
                                 select us).ToList();
                    if (query.Count > 0)
                    {
                        return Json(new { status = "1", Data = query }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    {
                        return Json(new { status = "0", Data = "Not found." }, JsonRequestBehavior.AllowGet);
                    }

                }


            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        public ActionResult updateSessions(int id)
        {
            try
            {
                using (linqDBContext db = new linqDBContext())
                {
                    var data = db.Lot_Issued.Where(x => x.id == id).Select(y => y.lotID).FirstOrDefault();
                    if (data != null)
                    {
                        Session.Add("lid", id);
                        Session.Add("lotID", data.Value);                       
                    }
                }
               
                return Json(new { status = "success", Data = "Done" }, JsonRequestBehavior.AllowGet);
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

    }
}