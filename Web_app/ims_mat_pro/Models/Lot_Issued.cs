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
    
    public partial class Lot_Issued
    {
        public int id { get; set; }
        public Nullable<int> lotID { get; set; }
        public Nullable<int> packet_A { get; set; }
        public Nullable<int> packet_B { get; set; }
        public Nullable<int> networkID { get; set; }
        public Nullable<int> localID { get; set; }
        public Nullable<System.DateTime> date { get; set; }
    }
}
