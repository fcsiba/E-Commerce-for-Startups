//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Manual changes to this file may cause unexpected behavior in your application.
//     Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace ims_mat_pro.Models
{
    using System;
    using System.Collections.Generic;
    
    public partial class tblItem
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
        public string tag { get; set; }
        public string scale { get; set; }
        public Nullable<System.DateTime> date { get; set; }
        public string coverImage { get; set; }
        public Nullable<int> lotId { get; set; }
        public string articleId { get; set; }
    }
}
