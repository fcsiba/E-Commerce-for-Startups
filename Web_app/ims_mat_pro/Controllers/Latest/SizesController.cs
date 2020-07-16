using ims_mat_pro.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Linq.Dynamic;

namespace ims_mat_pro.Controllers.Latest
{
    public class SizesController : Controller
    {

        linqDBContext dc = new linqDBContext();

        // GET: Sizes
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
                    dc.Configuration.LazyLoadingEnabled = false; // if your table is relational, contain foreign key
                    var v = (from a in abc.sizes
                             join c in abc.tblMainCategories
                             on a.mainCatID equals c.id
                             select new { id = a.id, size = a.size1, cat = c.name }).OrderBy(x => x.cat);


                    if (!(string.IsNullOrEmpty(sortColumn) && string.IsNullOrEmpty(sortColumnDir)))
                    {
                        v = v.OrderBy(sortColumn + " " + sortColumnDir).OrderBy(x => x.cat);
                    }

                    //Search   
                    var searchValue = Request.Form.GetValues("search[value]").FirstOrDefault();
                    if (!string.IsNullOrEmpty(searchValue))
                    {
                        v = v.Where(m => m.cat.Contains(searchValue)).OrderBy(x => x.cat);
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
        public ActionResult Edit(int id_, string size, int cat)
        {
            try
            {

                using (linqDBContext db = new linqDBContext())
                {
                    size query = (from us in db.sizes
                                  where us.id == id_
                                  select us).FirstOrDefault();

                    if (query != null)
                    {

                        //Calculating difference bw current values and updated values
                        query.size1 = size;
                        query.mainCatID = cat;
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
                    var query = (from us in db.sizes
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
        public ActionResult Add(string size, int cat)
        {
            try
            {
                int assignedID = 0;
                using (linqDBContext db = new linqDBContext())
                {
                    size lt = new size();
                    lt.mainCatID = cat;
                    lt.size1 = size;
                    db.sizes.Add(lt);
                    db.SaveChanges();
                    assignedID = lt.id;
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
                    var data = db.sizes.Single(x => x.id == id);

                    db.sizes.Remove(data);
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