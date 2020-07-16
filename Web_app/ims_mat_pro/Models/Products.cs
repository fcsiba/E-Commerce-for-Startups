using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ims_mat_pro.Models
{
    public class Products
    {
        public int id { get; set; }
        public string itemCode { get; set; }
        public string itemName { get; set; }
        public string fabric { get; set; }
        public Nullable<int> mainCategory { get; set; }
        public Nullable<int> subCategory { get; set; }
        public Nullable<decimal> originalPrice { get; set; }
        public Nullable<decimal> discount { get; set; }
        public Nullable<decimal> netPrice { get; set; }
        public string description { get; set; }       
        public Nullable<System.DateTime> date { get; set; }
        public string coverImage { get; set; }
        public Nullable<int> lotId { get; set; }
        public string articleId { get; set; }
        public List<string> sizes { get; set; }
    }
}