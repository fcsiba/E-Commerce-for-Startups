using ims_mat_pro.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Linq.Dynamic;

namespace ims_mat_pro.Controllers.Admin.Inventory
{
    public class InvController : Controller
    {
        linqDBContext dc = new linqDBContext();

        // GET: Inv
        public ActionResult Index()
        {
            using (linqDBContext db = new linqDBContext())
            {
                var network = (from us in db.tblRetails
                                where us.id != 2
                                select us).ToList();

                List<SelectListItem> item = new List<SelectListItem>();
                foreach (var itemm in network)
                {
                    item.Add(new SelectListItem
                    {
                        Text = itemm.name,
                        Value = itemm.id.ToString()
                    });
                }

                ViewBag.network = item;
            }


            return View();
        }

        public ActionResult Manage()
        {
            using (linqDBContext db = new linqDBContext())
            {
                var network = (from us in db.tblSubInventories
                               select us.networkID).Distinct().ToList();

                List<SelectListItem> item = new List<SelectListItem>();
                foreach (var itemm in network)
                {
                    var query = (from us in db.tblRetails
                                 where us.id == itemm.Value
                                 select us).FirstOrDefault();
                    if (query != null)
                    {
                        item.Add(new SelectListItem
                        {
                            Text = query.name,
                            Value = query.id.ToString()
                        });
                    }
                   
                }

                ViewBag.network = item;
            }


            return View();
        }

        [HttpPost]
        public ActionResult LoadInvResellers()
        {
            try
            {
                var netID = (from us in dc.vInvs
                             where us.networkID == 3
                             select us.issuedRetailId).FirstOrDefault();

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
                    var v = (from a in dc.tblSubInventories   
                             join item in dc.tblItems
                             on a.itemId equals item.id                         
                             where a.issueRetailId == netID
                             select new { a.id, code = item.itemCode, name = item.itemName, S = a.small, M = a.medium, L = a.large, XL = a.xLarge });

                    //SORT
                    if (!(string.IsNullOrEmpty(sortColumn) && string.IsNullOrEmpty(sortColumnDir)))
                    {
                        v = v.OrderBy(sortColumn + " " + sortColumnDir);
                    }

                    //Search   
                    var searchValue = Request.Form.GetValues("search[value]").FirstOrDefault();
                    if (!string.IsNullOrEmpty(searchValue))
                    {
                        v = v.Where(m => m.name.Contains(searchValue) ||
                                    m.code == searchValue);
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

        [HttpPost]
        public ActionResult LoadInvData()
        {
            try
            {
                Program.createTable("drop table temp_inv");
                Program.createTable("DECLARE @cols AS NVARCHAR(MAX), @query AS NVARCHAR(MAX) select @cols = STUFF((SELECT ',' + QUOTENAME(size) from Inventory_Latest group by size order by size FOR XML PATH(''), TYPE ).value('.', 'NVARCHAR(MAX)') ,1,1,'') set @query = 'SELECT id = IDENTITY(INT, 1, 1), itemId,' + @cols + ' into temp_inv from ( select itemId, qty, size from Inventory_Latest where network = 3 and localID = 0) x pivot ( sum(qty) for size in (' + @cols + ') ) p ' execute(@query);");
                Program.createTable("ALTER TABLE temp_inv ADD PRIMARY KEY ( id );");

                //var netID = (from us in dc.vInvs
                //             where us.networkID == 3
                //             select us.issuedRetailId).FirstOrDefault();



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
                    var v = (from a in dc.temp_inv
                             join item in dc.tblItems
                             on a.itemId equals item.id     
                             orderby a.itemId                     
                             select new { id = a.itemId, Code = item.itemCode, S = a.S, M = a.M, L = a.L, XL = a.XL,a.XXL, a.XXXL, K_14 = a.K_14, K_16 = a.K_16, K_18 = a.K_18, K_20 = a.K_20, K_22 = a.K_22, K_24 = a.K_24, K_26 = a.K_26, K_28 = a.K_28, _28 = a.C28, _30 = a.C30, _32 = a.C32, _34 = a.C34, _36 = a.C36, _38 = a.C38, _40 = a.C40, _42 = a.C42, _44 = a.C44 });

                   
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

        [HttpPost]
        public ActionResult LoadInvSaaj()
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
                    var v = (from a in dc.tblMainInventries    
                             join mc in dc.tblMainCategories
                             on a.mainCatId equals mc.id                        
                             select new { a.id, Category = mc.name, A = a.pacA, B = a.pacB, S = a.small, M = a.medium, L = a.large, XL = a.xLarge });

                    //SORT
                    if (!(string.IsNullOrEmpty(sortColumn) && string.IsNullOrEmpty(sortColumnDir)))
                    {
                        v = v.OrderBy(sortColumn + " " + sortColumnDir);
                    }

                    //Search   
                    var searchValue = Request.Form.GetValues("search[value]").FirstOrDefault();
                    if (!string.IsNullOrEmpty(searchValue))
                    {
                        v = v.Where(m => m.Category.Contains(searchValue));
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

        public ActionResult LoadInvIssued(int netID)
        {
            try
            {
                var rID = (from us in dc.vInvs
                             where us.networkID == netID
                             select us.issuedRetailId).FirstOrDefault();

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
                    var v = (from a in dc.tblIssues
                             join lot in dc.tblLots
                             on a.lotId equals lot.id
                             where a.issuedRetailId == rID                         
                             select new { a.id, Lot = lot.lotName, A = a.packetA, B = a.packetB, S = a.small, M = a.medium, L = a.large, XL = a.xLarge });

                    //SORT
                    if (!(string.IsNullOrEmpty(sortColumn) && string.IsNullOrEmpty(sortColumnDir)))
                    {
                        v = v.OrderBy(sortColumn + " " + sortColumnDir);
                    }

                    //Search   
                    var searchValue = Request.Form.GetValues("search[value]").FirstOrDefault();
                    if (!string.IsNullOrEmpty(searchValue))
                    {
                        v = v.Where(m => m.Lot.Contains(searchValue));
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

        public ActionResult ResellersInvTotal(int netID)
        {
            using (linqDBContext db = new linqDBContext())
            {
                string inv = "";
                try
                {
                    var v = (from a in dc.tblSubInventories                           
                             where a.networkID == netID
                             select a.small).Sum();

                    inv = "S = " + v + Environment.NewLine;
                }
                catch (Exception x)
                {

                }

                try
                {
                    var v = (from a in dc.tblSubInventories
                             where a.networkID == netID
                             select a.medium).Sum();

                    inv += "M = " + v + Environment.NewLine;
                }
                catch (Exception x)
                {

                }

                try
                {
                    var v = (from a in dc.tblSubInventories
                             where a.networkID == netID
                             select a.large).Sum();

                    inv += "L = " + v + Environment.NewLine;
                }
                catch (Exception x)
                {

                }

                try
                {
                    var v = (from a in dc.tblSubInventories
                             where a.networkID == netID
                             select a.xLarge).Sum();

                    inv += "XL = " + v + Environment.NewLine;
                }
                catch (Exception x)
                {

                }

                return Json(new { status = "success", Data = inv }, JsonRequestBehavior.AllowGet);

            }
        }

        public ActionResult PopulateStock(int id)
        {
            try
            {              
                using (linqDBContext db = new linqDBContext())
                {
                    var query = (from us in db.Inventory_Latest
                                 where us.itemId == id
                                 && us.network == 3
                                 && us.localID == 0
                                 select new { us.id, us.size, us.qty }).ToList();
                    if (query != null)
                    {
                        return Json(new { status = "success", Data = query }, JsonRequestBehavior.AllowGet);
                    }

                    return Json(new { status = "error", Data = "" }, JsonRequestBehavior.AllowGet);

                }
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }


        public ActionResult UpdateStock(int id, int[] ids, int[] vals, int count)
        {
            try
            {
                using (linqDBContext db = new linqDBContext())
                {
                    for (int i = 0; i < count; i++)
                    {
                        int _id = ids[i];
                        int _qty = vals[i];
                        var query = (from us in db.Inventory_Latest
                                     where us.id == _id
                                     select us).FirstOrDefault();
                        if (query != null)
                        {
                            query.qty = _qty;                          
                        }
                       
                    }
                    db.SaveChanges();

                    return Json(new { status = "success", Data = "Done" }, JsonRequestBehavior.AllowGet);

                }
            }
            catch (Exception x)
            {
                return Json(new { status = "error", Data = x.Message }, JsonRequestBehavior.AllowGet);
            }
        }
    }
}