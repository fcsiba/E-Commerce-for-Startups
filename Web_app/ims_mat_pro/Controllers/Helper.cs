using ims_mat_pro.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ims_mat_pro.Controllers
{
    public static class Helper
    {
        public static List<Packets_Scale> Get_Pack_Qty(int mainCatID)
        {         
            using (linqDBContext db = new linqDBContext())
            {
                var query = (from us in db.Packets_Scale
                             where us.mainCatID == mainCatID                             
                             select us).ToList();
                if (query != null)
                {
                    return query;
                }
            }

            return null;
        }
    }
}