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
    
    public partial class tblIssue
    {
        public int id { get; set; }
        public int issuedRetailId { get; set; }
        public Nullable<int> lotId { get; set; }
        public Nullable<int> packetA { get; set; }
        public Nullable<int> packetB { get; set; }
        public Nullable<int> small { get; set; }
        public Nullable<int> medium { get; set; }
        public Nullable<int> large { get; set; }
        public Nullable<int> xLarge { get; set; }
        public Nullable<System.DateTime> issueDate { get; set; }
        public Nullable<bool> isFromReturn { get; set; }
        public Nullable<int> networkID { get; set; }
    }
}
