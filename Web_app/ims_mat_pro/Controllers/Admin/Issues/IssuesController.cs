using ims_mat_pro.Models;
using System.Web.Mvc;
using System.Linq.Dynamic;

using System.Linq;
using System.Collections.Generic;
using System;

namespace ims_mat_pro.Controllers.Admin.Issues
{
    
    public class IssuesController : Controller
    {
        linqDBContext dc = new linqDBContext();

        // GET: Issues
        public ActionResult Index()
        {

            var t = (from x in dc.tblRetails where x.id!=1 && x.id != 2 
                     select x).ToList();
            var L= (from x in dc.tblLots
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
        //add
        public ActionResult add( string retailID, string local, string lot,string localname, string pacA, string pacB, string small, string med, string large, string xLarge,string date,string isFromReturn)
        {
            //check if issued retail id already exist in the table issuedretailer
            tblIssue lt = new tblIssue();
            issuedRetailer ir ;
            try
            {
                int assignedID = 0;
                int retailId = Convert.ToInt32(retailID);
                int loc = Convert.ToInt32(local);
                int lotid = Convert.ToInt32(lot);
                using (linqDBContext db = new linqDBContext())
                {
                    var checkRetail=(from a in db.issuedRetailers where a.localId==loc && a.retailId==retailId select a).FirstOrDefault();

                    if (checkRetail == null)
                    {
                        ir = new issuedRetailer();
                        ir.retailId = retailId;
                        ir.localId = loc;
                        ir.localName = localname;
                        db.issuedRetailers.Add(ir);
                        db.SaveChanges();
                    }
                    else
                    {
                        ir = checkRetail;
                    }
                
                    lt.lotId = lotid;
                    lt.networkID = retailId;
                    lt.packetA = Convert.ToInt32(pacA);
                    lt.packetB = Convert.ToInt32(pacB);
                    lt.small = Convert.ToInt32(small);
                    lt.medium = Convert.ToInt32(med);
                    lt.large = Convert.ToInt32(large);
                    lt.xLarge = Convert.ToInt32(xLarge);
                    lt.issueDate = Convert.ToDateTime(date).Date;
                    if (isFromReturn == "0")
                    {
                        lt.isFromReturn = false;

                    }
                    else
                    {
                        lt.isFromReturn = true;

                    }

                        var mainCatId = (from a in dc.tblLots
                                                        where a.id == lotid
                                                        select a.mainCatId).ToList().First().Value;
                    int chkCatId = Convert.ToInt32(mainCatId);

                        var mainInv = (from b in dc.tblMainInventries
                                                    where b.mainCatId == chkCatId
                                                   select b).FirstOrDefault();
                    if (mainInv == null)
                    {

                        throw new Exception("This Category doest not exisit in main inventory!");
                    }


                    int totalS = 0, totalM = 0, totalL = 0, totalXL = 0, tPacA = 0, tPacB = 0;

                    //Previous Qtys
                    try
                    {
                        var ts = (from a in db.tblIssues
                                  where a.lotId  == lotid
                                  select a.small.Value).Sum();
                        if (ts > 0)
                        {
                            totalS = ts;
                        }
                    }
                    catch (Exception xz)
                    { }                  
                    try
                    {
                        var tm = (from a in db.tblIssues
                                  where a.lotId == lotid
                                  select a.medium.Value).Sum();
                        if (tm > 0)
                        {
                            totalM = tm;
                        }
                    }
                    catch (Exception xz)
                    { }
                    try
                    {
                        var tl = (from a in db.tblIssues
                                  where a.lotId == lotid
                                  select a.large.Value).Sum();
                        if (tl > 0)
                        {
                            totalL = tl;
                        }
                    }
                    catch (Exception xz)
                    { }

                    try
                    {
                        var tXL = (from a in db.tblIssues
                                   where a.lotId == lotid
                                   select a.xLarge.Value).Sum();
                        if (tXL > 0)
                        {
                            totalL = tXL;
                        }
                    }
                    catch (Exception xz)
                    { }
                    try
                    {
                        var tA = (from a in db.tblIssues
                                  where a.lotId == lotid
                                  select a.packetA.Value).Sum();
                        if (tA > 0)
                        {
                            tPacA = tA;
                        }
                    }
                    catch (Exception xz)
                    { }
                    try
                    {
                        var tB = (from a in db.tblIssues
                                  where a.lotId == lotid
                                  select a.packetB.Value).Sum();
                        if (tB > 0)
                        {
                            tPacB = tB;
                        }
                    }
                    catch (Exception xz)
                    { }


                    //Check lot
                    var lotQuery = (from us in db.tblLots
                                    where us.id == lotid
                                    select us).FirstOrDefault();
                    if (lotQuery != null)
                    {
                        if ((lt.packetA + tPacA) > lotQuery.packets_A)
                        {
                            throw new Exception("Cannot add more than" + (lotQuery.packets_A.Value - tPacA) + " A packets.");
                        }
                        if ((lt.packetB + tPacB) > lotQuery.packets_B)
                        {
                            throw new Exception("Cannot add more than" + (lotQuery.packets_B.Value - tPacB) + " B packets.");
                        }
                        if ((lt.small + totalS) > lotQuery.small)
                        {
                            throw new Exception("Cannot add more than" + (lotQuery.small.Value - totalS) + " small items.");
                        }
                        if ((lt.medium + totalM) > lotQuery.medium)
                        {
                            throw new Exception("Cannot add more than" + (lotQuery.medium.Value - totalM) + " medium items.");
                        }
                        if ((lt.large + totalL) > lotQuery.large)
                        {
                            throw new Exception("Cannot add more than" + (lotQuery.large.Value - totalL) + " large items.");
                        }
                        if ((lt.xLarge + totalXL) > lotQuery.xLarge)
                        {
                            throw new Exception("Cannot add more than" + (lotQuery.xLarge.Value - totalXL) + " XL items.");
                        }

                        //update main invenrty 
                        var query = mainInv;
                        if (query.pacA >= lt.packetA)
                        {
                            query.pacA -= lt.packetA;


                        }
                        else
                        {
                            throw new Exception("Inventry have only " + query.pacA + "  Packet A item(s) left.");
                        }

                        if (query.pacB >= lt.packetB)
                        {
                            query.pacB -= lt.packetB;


                        }
                        else
                        {
                            throw new Exception("Inventry have only  " + query.pacB + " Packet B Item(s) left.");
                        }

                        if (query.small >= lt.small)
                        {
                            query.small -= lt.small;


                        }
                        else
                        {
                            throw new Exception("Inventry have only " + query.small + "  small item(s) left.");
                        }
                        if (query.medium >= lt.medium)
                        {
                            query.medium -= lt.medium;


                        }
                        else
                        {
                            throw new Exception("Inventry have only " + query.medium + "  Medium  item(s)");
                        }
                        if (query.large >= lt.large)
                        {
                            query.large -= lt.large;


                        }
                        else
                        {
                            throw new Exception("Inventry have only " + query.large + " Large item(s)");
                        }
                        if (query.xLarge >= lt.xLarge)
                        {
                            query.xLarge -= lt.xLarge;


                        }
                        else
                        {
                            throw new Exception("inventry have only" + query.xLarge + " xLarge item(s)");
                        }

                        dc.SaveChanges();


                        //saved all changes

                        lt.issuedRetailId = ir.Id;
                        db.tblIssues.Add(lt);
                        db.SaveChanges();
                        assignedID = lt.id;

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

        //DELETE
        public ActionResult Delete(int id)
        {
            try
            {
                using (linqDBContext db = new linqDBContext())
                {

                    var data = db.tblLots.Single(x => x.id == id);
                    tblMainInventry mainInventry = (from us in db.tblMainInventries
                                                    where us.mainCatId == data.mainCatId
                                                    select us).FirstOrDefault();
                    //applying deletion to main inventry first
                    mainInventry.pacA += data.packets_A;
                    mainInventry.pacB += data.packets_B;
                    mainInventry.small += data.small;
                    mainInventry.medium += data.medium;
                    mainInventry.large += data.large;
                    mainInventry.xLarge += data.xLarge;
                    db.tblLots.Remove(data);
                    db.SaveChanges();
                    return Json(new { status = "success", Data = "done" }, JsonRequestBehavior.AllowGet);

                }


            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }


        //edit

        public ActionResult Edit(string id_, string cat, string local,string lot,string localname, string pacA, string pacB, string small, string med, string large, string xLarge,string date,string isFromReturn)
        {
            try
            {

                int catId = Convert.ToInt32(cat);
                int id = Convert.ToInt32(id_);
                int loc = Convert.ToInt32(local);
                int lotid = Convert.ToInt32(lot);
                int uPacA, uPacB, uSmall, uMed, uLarge, uxLarge;

                using (linqDBContext db = new linqDBContext())
                {
                    var query = (from us in db.tblIssues
                                 where us.id == id
                                 select us).FirstOrDefault();

                    var issuedret= (from us in db.issuedRetailers
                                    where us.Id == query.issuedRetailId 
                                    select us).FirstOrDefault();

                    var getLotId= (from us in db.tblLots
                                   where us.id == query.lotId
                                   select us).FirstOrDefault().mainCatId;
                    tblMainInventry mainInventry = (from us in db.tblMainInventries
                                                    where us.mainCatId == getLotId  
                                                    select us).FirstOrDefault();
                    if (query != null)
                    {

                        int totalS = 0, totalM = 0, totalL = 0, totalXL = 0, tPacA = 0, tPacB = 0;

                        //Previous Qtys
                        try
                        {
                            var ts = (from a in db.tblIssues
                                      where 
                                      a.lotId == lotid
                                      && a.id != id
                                      select a.small.Value).Sum();
                            if (ts > 0)
                            {
                                totalS = ts;
                            }
                        }
                        catch (Exception xz)
                        { }
                        try
                        {
                            var tm = (from a in db.tblIssues
                                      where
                                       a.lotId == lotid
                                      && a.id != id
                                      select a.medium.Value).Sum();
                            if (tm > 0)
                            {
                                totalM = tm;
                            }
                        }
                        catch (Exception xz)
                        { }
                        try
                        {
                            var tl = (from a in db.tblIssues
                                      where
                                      a.lotId == lotid
                                      && a.id != id
                                      select a.large.Value).Sum();
                            if (tl > 0)
                            {
                                totalL = tl;
                            }
                        }
                        catch (Exception xz)
                        { }

                        try
                        {
                            var tXL = (from a in db.tblIssues
                                       where
                                     a.lotId == lotid
                                    && a.id != id
                                       select a.xLarge.Value).Sum();
                            if (tXL > 0)
                            {
                                totalL = tXL;
                            }
                        }
                        catch (Exception xz)
                        { }
                        try
                        {
                            var tA = (from a in db.tblIssues
                                      where
                                      a.lotId == lotid
                                      && a.id != id
                                      select a.packetA.Value).Sum();
                            if (tA > 0)
                            {
                                tPacA = tA;
                            }
                        }
                        catch (Exception xz)
                        { }
                        try
                        {
                            var tB = (from a in db.tblIssues
                                      where
                                     a.lotId == lotid
                                     && a.id != id
                                      select a.packetB.Value).Sum();
                            if (tB > 0)
                            {
                                tPacB = tB;
                            }
                        }
                        catch (Exception xz)
                        { }

                        //Check lot
                        var lotQuery = (from us in db.tblLots
                                        where us.id == lotid
                                        select us).FirstOrDefault();
                        if (lotQuery != null)
                        {
                            if (Convert.ToInt32(pacA) + tPacA > lotQuery.packets_A)
                            {
                                throw new Exception("Cannot add more than " + (lotQuery.packets_A - tPacA) + " A packets.");
                            }
                            if (Convert.ToInt32(pacB) + tPacB > lotQuery.packets_B)
                            {
                                throw new Exception("Cannot add more than " + (lotQuery.packets_B - tPacB) + " B packets.");
                            }
                            if (Convert.ToInt32(small) + totalS > lotQuery.small)
                            {
                                throw new Exception("Cannot add more than " + (lotQuery.small - totalS) + " small items.");
                            }
                            if (Convert.ToInt32(med) + totalM > lotQuery.medium)
                            {
                                throw new Exception("Cannot add more than " + (lotQuery.medium - totalM) + " medium items.");
                            }
                            if (Convert.ToInt32(large) + totalL > lotQuery.large)
                            {
                                throw new Exception("Cannot add more than " + (lotQuery.large - totalL) + " large items.");
                            }
                            if (Convert.ToInt32(xLarge) + totalXL > lotQuery.xLarge)
                            {
                                throw new Exception("Cannot add more than " + (lotQuery.xLarge - totalXL) + " XL items.");
                            }

                            //applying changes to main inventry
                            uPacA = Convert.ToInt32(pacA) > 0 ? Convert.ToInt32(pacA) - (int)(query.packetA) : (int)(query.packetA);
                            uPacB = Convert.ToInt32(pacB) > 0 ? Convert.ToInt32(pacB) - (int)(query.packetB) : (int)(query.packetB);

                            uSmall = Convert.ToInt32(small) > 0 ? Convert.ToInt32(small) - (int)(query.small) : (int)(query.small);

                            uMed = Convert.ToInt32(med) > 0 ? Convert.ToInt32(med) - (int)(query.medium) : (int)(query.medium);

                            uLarge = Convert.ToInt32(large) > 0 ? Convert.ToInt32(large) - (int)(query.large) : (int)(query.large);

                            uxLarge = Convert.ToInt32(xLarge) > 0 ? Convert.ToInt32(xLarge) - (int)(query.xLarge) : (int)(query.xLarge);

                            //Applying Changes to main inventry
                            if (mainInventry.pacA >= uPacA)
                            {
                                mainInventry.pacA -= uPacA;

                            }
                            else
                            {

                                throw new Exception("Inventory have only " + mainInventry.pacA + " item(s)  for packet A");
                            }
                            if (mainInventry.pacB >= uPacB)
                            {
                                mainInventry.pacB -= uPacB;

                            }
                            else
                            {

                                throw new Exception("Inventory  have only " + mainInventry.pacB + " item(s)  for packet B");
                            }
                            if (mainInventry.small >= uSmall)
                            {
                                mainInventry.small -= uSmall;

                            }
                            else
                            {

                                throw new Exception("Inventory have only " + mainInventry.small + " item(s)  for small");
                            }
                            if (mainInventry.medium >= uMed)
                            {
                                mainInventry.medium -= uMed;

                            }
                            else
                            {

                                throw new Exception("Inventory have only " + mainInventry.medium + " item(s)  for medium");
                            }
                            if (mainInventry.large >= uLarge)
                            {
                                mainInventry.large -= uLarge;

                            }
                            else
                            {

                                throw new Exception("Inventory have only " + mainInventry.large + " item(s)  for large");
                            }
                            if (mainInventry.xLarge >= uxLarge)
                            {
                                mainInventry.xLarge -= uxLarge;

                            }
                            else
                            {

                                throw new Exception("Inventory have only " + mainInventry.xLarge + " item(s)  for xLarge");
                            }
                            mainInventry.pacB -= uPacB;
                            mainInventry.small -= uSmall;
                            mainInventry.medium -= uMed;
                            mainInventry.large -= uLarge;
                            mainInventry.xLarge -= uxLarge;


                            //applying changes to issue entry
                            issuedret.retailId = catId;
                            issuedret.localId = loc;
                            issuedret.localName = localname;
                            query.lotId = lotid;
                            query.packetA = Convert.ToInt32(pacA);
                            query.packetB = Convert.ToInt32(pacB);
                            query.small = Convert.ToInt32(small);
                            query.medium = Convert.ToInt32(med);
                            query.large = Convert.ToInt32(large);
                            query.xLarge = Convert.ToInt32(xLarge);
                            query.issueDate = Convert.ToDateTime(date).Date;
                            if (isFromReturn == "0")
                            {
                                query.isFromReturn = false;

                            }
                            else
                            {
                                query.isFromReturn = true;

                            }

                            db.SaveChanges();


                        }
                           

                        return Json(new JsonResult()
                        {
                            Data = "Success"
                        }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    {
                        return Json(new JsonResult()
                        {
                            Data = "error"
                        }, JsonRequestBehavior.AllowGet);
                    }
                }


            }
            catch (Exception x)
            {

               return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }
    
        //itemList

        public JsonResult itemList(string Id)
        {
 
            int id_ = Convert.ToInt32(Id);

            if (id_ == 3) //Resellers
            {
                var main = (from s in dc.tblResellers
                                select new {value=s.id,Text=s.name }).ToArray();
                SelectListItem sli = new SelectListItem();
                return Json(new SelectList(main.ToArray(), "Value", "Text"), JsonRequestBehavior.AllowGet);


            }
            else if (id_ == 4) //Outlets
            {
                var mainList = (from s in dc.tblOutlets
                                select new { value = s.id, Text = s.OutletName }).ToArray();
                return Json(new SelectList(mainList.ToArray(), "Value", "Text"), JsonRequestBehavior.AllowGet);

            }           
            else if (id_ == 6) //Wholesellers
            {
                var mainList = (from s in dc.tblWholesellers
                                select new { value = s.id, Text = s.Name }).ToArray();
                return Json(new SelectList(mainList.ToArray(), "Value", "Text"), JsonRequestBehavior.AllowGet);
            }
            else if (id_ == 9) //Distributors
            {
                var mainList = (from s in dc.tblDistributors
                                select new { value = s.id, Text = s.Name }).ToArray();
                return Json(new SelectList(mainList.ToArray(), "Value", "Text"), JsonRequestBehavior.AllowGet);
            }
            else if (id_ == 10) //Online store
            {
                //var mainList = (from s in dc.
                //                select s).ToList();
                //return Json(new SelectList(mainList.ToArray(), "id", "itemName"), JsonRequestBehavior.AllowGet);
            }


            return null;
        }
 


        //Load DATA
        public ActionResult LoadData()
        {
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
                  

                    
                    var v = (from a in abc.tblIssues 
                             join c in abc.issuedRetailers on a.issuedRetailId equals c.Id
                             join b in abc.tblRetails on c.retailId equals b.id
                             join d in abc.tblLots on a.lotId equals d.id
                             select new { id = a.id, type = b.name,name=c.localName, lot = d.lotName,pacA=a.packetA,pacB=a.packetB,small=a.small,med=a.medium,large=a.large,xlarge=a.xLarge, issueDate = a.issueDate.Value.Day + "/" + a.issueDate.Value.Month + "/" + a.issueDate.Value.Year, isFromReturn = a.isFromReturn });


                    if (!(string.IsNullOrEmpty(sortColumn) && string.IsNullOrEmpty(sortColumnDir)))
                    {
                        v = v.OrderBy(sortColumn + " " + sortColumnDir);
                    }

                    //Search   
                    var searchValue = Request.Form.GetValues("search[value]").FirstOrDefault();
                    //if (!string.IsNullOrEmpty(searchValue))
                    //{
                    //    v = v.Where(m => m.lot.Contains(searchValue) ||
                    //                m.id.ToString() == searchValue || m.Qnty.ToString() == searchValue);
                    //}

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
        //populate
        public ActionResult Populate(int id)
        {
            try
            {
                using (linqDBContext db = new linqDBContext())
                {
                    var query = (from us in db.tblIssues
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


        public ActionResult updateSession(int id,string lotId)
        {
            try
            {
                int _lotid = (from a in dc.tblIssues where a.id==id select a.lotId).First().Value;
                if (_lotid==0)
                {
                    throw new Exception("Lot Doesnt Belong to The Item Category");
                }

                    Session.Add("sessionIssueID",id);

                Session.Add("sessionlotid", _lotid);
                

                return Json(new { status = "success", Data = id }, JsonRequestBehavior.AllowGet);

               


            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }


        public ActionResult LoadDataNA()
        {
            int utype = Convert.ToInt32(Session["uType"]);
                int uId= Convert.ToInt32(Session["localID"]);


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



                    var v = (from a in abc.tblIssues
                             join c in abc.issuedRetailers on a.issuedRetailId equals c.Id
                             where c.retailId == utype && c.localId == uId
                             join b in abc.tblRetails on c.retailId equals b.id
                             join d in abc.tblLots on a.lotId equals d.id
                            
                             select new { id = a.id, type = b.name, name = c.localName, lot = d.lotName, pacA = a.packetA, pacB = a.packetB, small = a.small, med = a.medium, large = a.large, xlarge = a.xLarge, issueDate = a.issueDate, isFromReturn = a.isFromReturn });


                    if (!(string.IsNullOrEmpty(sortColumn) && string.IsNullOrEmpty(sortColumnDir)))
                    {
                        v = v.OrderBy(sortColumn + " " + sortColumnDir);
                    }

                    //Search   
                    var searchValue = Request.Form.GetValues("search[value]").FirstOrDefault();
                    //if (!string.IsNullOrEmpty(searchValue))
                    //{
                    //    v = v.Where(m => m.lot.Contains(searchValue) ||
                    //                m.id.ToString() == searchValue || m.Qnty.ToString() == searchValue);
                    //}

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





    }
}