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
    
    public partial class tblReseller
    {
        public int id { get; set; }
        public string name { get; set; }
        public string fatherName { get; set; }
        public string NIC { get; set; }
        public string address { get; set; }
        public string Mob { get; set; }
        public Nullable<bool> isActive { get; set; }
        public Nullable<System.DateTime> DOA { get; set; }
        public Nullable<int> supervisor { get; set; }
        public Nullable<bool> isSupervisor { get; set; }
    }
}
