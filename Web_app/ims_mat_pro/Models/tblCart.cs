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
    
    public partial class tblCart
    {
        public int id { get; set; }
        public string userMob { get; set; }
        public Nullable<int> itemID { get; set; }
        public Nullable<decimal> itemQty { get; set; }
        public Nullable<decimal> Price { get; set; }
        public string size { get; set; }
    }
}