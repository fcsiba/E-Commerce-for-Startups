using ims_mat_pro.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Linq.Dynamic;


namespace ims_mat_pro.Controllers.Admin.Lot
{
    public class LotController : Controller
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
                    var v = (from a in abc.tblLots  join b in abc.tblMainCategories on a.mainCatId equals b.id
                    select new { id=a.id,Category= b.name, lotName = a.lotName,packetA= a.packets_A, packetB = a.packets_B, small=a.small,medium=a.medium,large=a.large,xlarge=a.xLarge});

                   
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
        public ActionResult Edit(string id_, string cat, string name, string pacA, string pacB, string small, string med, string large, string xLarge)
        {
            try
            {

                int catId = Convert.ToInt32(cat);
                int id = Convert.ToInt32(id_);
                int uPacA, uPacB, uSmall, uMed, uLarge, uxLarge;
                using (linqDBContext db = new linqDBContext())
                {
                    tblLot query = (from us in db.tblLots
                                 where us.id == id
                                 select us).FirstOrDefault();
                    tblMainInventry mainInventry= (from us in db.tblMainInventries
                                       where us.mainCatId == catId
                                       select us).FirstOrDefault();
                    if (query != null)
                    {

                        //Calculating difference bw current values and updated values
                        uPacA = Convert.ToInt32(pacA) > 0 ? Convert.ToInt32(pacA) - (int)(query.packets_A) : (int)(query.packets_A);
                        uPacB = Convert.ToInt32(pacB) > 0 ? Convert.ToInt32(pacB) - (int)(query.packets_B) : (int)(query.packets_B);

                        uSmall = Convert.ToInt32(small) > 0 ? Convert.ToInt32(small) - (int)(query.small) : (int)(query.small);

                        uMed = Convert.ToInt32(med) > 0 ? Convert.ToInt32(med) - (int)(query.medium) : (int)(query.medium);

                        uLarge = Convert.ToInt32(large) > 0 ? Convert.ToInt32(large) - (int)(query.large) : (int)(query.large);

                        uxLarge = Convert.ToInt32(xLarge) > 0 ? Convert.ToInt32(xLarge) - (int)(query.xLarge) : (int)(query.xLarge);

                        //Applying Changes to main inventry
                        mainInventry.pacA += uPacA;
                        mainInventry.pacB += uPacB;
                        mainInventry.small += uSmall;
                        mainInventry.medium += uMed;
                        mainInventry.large += uLarge;
                        mainInventry.xLarge += uxLarge;
                        //apply changes to lot entry
                        query.mainCatId = catId;
                        query.lotName = name;
                        query.packets_A =Convert.ToInt32(pacA);
                        query.packets_B = Convert.ToInt32( pacB);
                        query.small = Convert.ToInt32(small);
                        query.medium = Convert.ToInt32 (med);
                        query.large = Convert.ToInt32(large);
                        query.xLarge = Convert.ToInt32(xLarge);
                  
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
                    var query = (from us in db.tblLots
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

        //ADD
        public ActionResult Add(string cat, string name, string pacA, string pacB, string small, string med, string large, string xLarge)
        {
            try
            {
                int assignedID = 0;
                using (linqDBContext db = new linqDBContext())
                {
                    tblLot lt = new tblLot();
                    lt.mainCatId =Convert.ToInt32(cat);
                    lt.lotName = name;
                    lt.packets_A = Convert.ToInt32(pacA);
                    lt.packets_B = Convert.ToInt32 (pacB);
                    lt.small = Convert.ToInt32(small);
                    lt.medium = Convert.ToInt32 (med);
                    lt.large = Convert.ToInt32( large);
                    lt.xLarge = Convert.ToInt32 (xLarge);

                    db.tblLots.Add(lt);
                    db.SaveChanges();
                    assignedID = lt.id;
                    //update main inventry
                    //var mainCatId = (from a in dc.tblLots
                    //                 where a.id == lt.mainCatId
                    //                 select a.mainCatId).ToList().First().Value;
                    //int chkCatId = Convert.ToInt32(lt.mainCatId);

                    var chkqry = (from b in dc.tblMainInventries
                                  where b.mainCatId == lt.mainCatId
                                  select b).FirstOrDefault();

                    if (chkqry == null)
                    {
                        tblMainInventry mI = new tblMainInventry();
                        mI.mainCatId = lt.mainCatId;
                        mI.pacA = lt.packets_A;
                        mI.pacB = lt.packets_B;
                        mI.small = lt.small;
                        mI.medium = lt.medium;
                        mI.large = lt.large;
                        mI.xLarge = lt.xLarge;

                        dc.tblMainInventries.Add(mI);
                        dc.SaveChanges();


                    }
                    else
                    {
                        //update main invenrty                       
                        chkqry.pacA += lt.packets_A;
                        chkqry.pacB += lt.packets_B;
                        chkqry.small += lt.small;
                        chkqry.medium += lt.medium;
                        chkqry.large += lt.large;
                        chkqry.xLarge += lt.xLarge;

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
                    mainInventry.pacA -= data.packets_A;
                    mainInventry.pacB -= data.packets_B;
                    mainInventry.small -= data.small;
                    mainInventry.medium -= data.medium;
                    mainInventry.large -= data.large;
                    mainInventry.xLarge -= data.xLarge;
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
    }
}