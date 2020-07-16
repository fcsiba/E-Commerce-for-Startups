using ims_mat_pro.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Linq.Dynamic;

namespace ims_mat_pro.Controllers.Latest
{
    public class NewLotController : Controller
    {
        linqDBContext dc = new linqDBContext();

        // GET: Lot
        public ActionResult Index()
        {
            var maincat = (from us in dc.tblMainCategories
                           select us).ToList();

            List<SelectListItem> maincatitem = new List<SelectListItem>();

            foreach (var itemm in maincat)
            {
                maincatitem.Add(new SelectListItem
                {
                    Text = itemm.name,
                    Value = itemm.id.ToString()
                });
            }


            ViewBag.maincat = maincatitem;
            return View();
        }

        //LOAD DATA
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
                    var v = (from a in abc.Lots
                             join b in abc.tblMainCategories on a.mainCatId equals b.id
                             select new { id = a.id, Category = b.name, lotName = a.lotName, packetA = a.A, packetB = a.B });


                    if (!(string.IsNullOrEmpty(sortColumn) && string.IsNullOrEmpty(sortColumnDir)))
                    {
                        v = v.OrderBy(sortColumn + " " + sortColumnDir);
                    }

                    //Search   
                    var searchValue = Request.Form.GetValues("search[value]").FirstOrDefault();
                    if (!string.IsNullOrEmpty(searchValue))
                    {
                        v = v.Where(m => m.lotName.Contains(searchValue) ||
                                    m.id.ToString() == searchValue || m.Category.ToString() == searchValue);
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


        //EDIT
        public ActionResult Edit(string id_, string cat, string name, string pacA, string pacB)
        {
            try
            {

                int catId = Convert.ToInt32(cat);
                int id = Convert.ToInt32(id_);               
                using (linqDBContext db = new linqDBContext())
                {
                    Lot query = (from us in db.Lots
                                    where us.id == id
                                    select us).FirstOrDefault();

                    Main_Inventory mainInventry = (from us in db.Main_Inventory
                                                    where us.mainCatID == catId
                                                    select us).FirstOrDefault();
                    if (query != null)
                    {
                        int diffA = Convert.ToInt32(pacA) - query.A.Value;
                        int diffB = Convert.ToInt32(pacB) - query.B.Value;

                        //Applying Changes to main inventry
                        mainInventry.packet_A += diffA;
                        mainInventry.packet_B += diffB;                   
                        
                        query.lotName = name;
                        query.A = Convert.ToInt32(pacA);
                        query.B = Convert.ToInt32(pacB);                      
                        db.SaveChanges();

                        return Json(new JsonResult()
                        {
                            Data = "Success"
                        }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    {
                        return Json(new JsonResult()
                        {
                            Data = "Not found!"
                        }, JsonRequestBehavior.AllowGet);
                    }
                }


            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        //POPULATE
        public ActionResult Populate(int id)
        {
            try
            {
                using (linqDBContext db = new linqDBContext())
                {
                    var query = (from us in db.Lots
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

                    var query = (from us in db.Lot_Loose_Items
                                 where us.lotID == id
                                 select us).ToList();
                    if (query.Count > 0)
                    {
                        return Json(new { status = "1", Data = query }, JsonRequestBehavior.AllowGet);             
                    }
                    else
                    {
                        var sQuery = (from us in db.sizes
                                      where us.mainCatID == mainID
                                      select us).ToList();
                        if (sQuery != null)
                        {
                            return Json(new { status = "2", Data = sQuery }, JsonRequestBehavior.AllowGet);
                        }

                        return Json(new { status = "0", Data = "Not found." }, JsonRequestBehavior.AllowGet);
                    }

                }


            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        //ADD
        public ActionResult Add(string cat, string name, string pacA, string pacB)
        {
            try
            {
                int assignedID = 0;
                using (linqDBContext db = new linqDBContext())
                {
                    Lot lt = new Lot();
                    lt.mainCatId = Convert.ToInt32(cat);
                    lt.lotName = name;
                    lt.A = Convert.ToInt32(pacA);
                    lt.B = Convert.ToInt32(pacB);
                   
                    db.Lots.Add(lt);
                    db.SaveChanges();

                    assignedID = lt.id;
                  

                    var chkqry = (from b in dc.Main_Inventory
                                  where b.mainCatID == lt.mainCatId
                                  select b).FirstOrDefault();

                    if (chkqry == null)
                    {
                        Main_Inventory mI = new Main_Inventory();
                        mI.mainCatID = lt.mainCatId;
                        mI.packet_A = lt.A;
                        mI.packet_B = lt.B;
                        dc.Main_Inventory.Add(mI);
                        dc.SaveChanges();


                    }
                    else
                    {
                        //update main invenrty                       
                        chkqry.packet_A += lt.A;
                        chkqry.packet_B += lt.B;
                        dc.SaveChanges();
                    }



                }

                return Json(new JsonResult()
                {
                    Data = assignedID
                }, JsonRequestBehavior.AllowGet);
            }
            catch (ApplicationException m)
            {
                return Json(new { status = "error", Data = m.Message }, JsonRequestBehavior.AllowGet);

            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        //ADD
        public ActionResult SaveLooseStock(int lotID, int[] dIDs, string[] sizes, int[] vals, bool add, int count)
        {
            try
            {                
                using (linqDBContext db = new linqDBContext())
                {
                    var mainCatID = db.Lots.Where(x => x.id == lotID).Select(y => y.mainCatId.Value).FirstOrDefault();

                    if (add)
                    {                        
                        for (int i = 0; i < count; i++)
                        {
                            Lot_Loose_Items a = new Lot_Loose_Items();
                            a.lotID = lotID;
                            a.size = sizes[i];
                            a.qty = vals[i];
                            db.Lot_Loose_Items.Add(a);

                            //Main_Inv_Loose
                            var chkqry = (from b in db.Main_Inventory_Loose
                                          where b.mainCatID == mainCatID
                                          && b.size == a.size
                                          select b).FirstOrDefault();
                            if (chkqry == null)
                            {
                                Main_Inventory_Loose m = new Main_Inventory_Loose();
                                m.size = sizes[i];
                                m.mainCatID = mainCatID;
                                m.qty = vals[i];
                                db.Main_Inventory_Loose.Add(m);
                            }
                            else
                            {
                                chkqry.qty += vals[i];
                            }
                        }
                        db.SaveChanges();
                    }
                    else
                    {
                        for (int i = 0; i < count; i++)
                        {
                            int dID = dIDs[i];
                            //Lot Loose Items
                            var query = (from us in db.Lot_Loose_Items
                                         where us.id == dID
                                         select us).FirstOrDefault();
                            if (query != null)
                            {
                                string size = sizes[i];
                                //Main_Inv_Loose
                                var loose_inv = (from b in db.Main_Inventory_Loose
                                              where b.mainCatID == mainCatID
                                              && b.size == size
                                              select b).FirstOrDefault();
                                if (loose_inv == null)
                                {
                                    Main_Inventory_Loose m = new Main_Inventory_Loose();
                                    m.size = sizes[i];
                                    m.mainCatID = mainCatID;
                                    m.qty = vals[i];
                                    db.Main_Inventory_Loose.Add(m);
                                }
                                else
                                {
                                    int diff = vals[i] - query.qty.Value;
                                    loose_inv.qty = loose_inv.qty + diff;                                   
                                }

                                //Update loose lot stocks
                                query.qty = vals[i];                               
                            }
                        }
                        db.SaveChanges();
                    }
                }

                return Json(new { status = "success", Data = "Done!" }, JsonRequestBehavior.AllowGet);
            }
            catch (ApplicationException m)
            {
                return Json(new { status = "error", Data = m.Message }, JsonRequestBehavior.AllowGet);

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

                    var data = db.Lots.Single(x => x.id == id);

                    //Lot Loose Stock
                    var queryLoose = (from us in db.Lot_Loose_Items
                                      where us.lotID == data.id
                                      select us).ToList();
                    if (queryLoose != null)
                    {
                        foreach (var item in queryLoose)
                        {
                            //Update Loose inventory
                            var looseInv = (from us in db.Main_Inventory_Loose
                                            where us.mainCatID == data.mainCatId
                                            && us.size == item.size
                                            select us).FirstOrDefault();
                            if (looseInv != null)
                            {
                                looseInv.qty -= item.qty;                             
                            }                           
                        }
                    }


                    var mainInventry = (from us in db.Main_Inventory
                                        where us.mainCatID == data.mainCatId
                                        select us).FirstOrDefault();
                    if (mainInventry != null)
                    {                      
                        mainInventry.packet_A -= data.A;
                        mainInventry.packet_B -= data.B;
                    }
                    
                    db.Lots.Remove(data);
                    db.SaveChanges();
                    return Json(new { status = "success", Data = "done" }, JsonRequestBehavior.AllowGet);

                }


            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }
    }
}