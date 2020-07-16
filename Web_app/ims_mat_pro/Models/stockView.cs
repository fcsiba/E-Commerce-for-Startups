using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace ims_mat_pro.Models
{
    public class stockView
    {
        [Required]
        [Display(Name = "type")]

        public ICollection<System.Web.Mvc.SelectListItem> Type { get; set; }
        [Required]
        [Display(Name = "retailer")]

        public ICollection<System.Web.Mvc.SelectListItem> Retailer { get; set; }
        [Required]
        [Display(Name = "category")]

        public ICollection<System.Web.Mvc.SelectListItem> Category { get; set; }


        [Required]
        [Display(Name = "Packet A")]
        public string A { get; set; }


     
        [Display(Name = "Packet B")]
        public int B { get; set; }

        [Required]
        [Display(Name = "Small")]
        public int small { get; set; }

        [Required]
        
        [Display(Name = "Medium")]
        public int Medium { get; set; }

        [Required]
        [Display(Name = "Email")]
        public int Large { get; set; }


        [Required]
        [Display(Name = "X Large")]
        public int xLarge { get; set; }


        [Required]
        [Display(Name = "Date")]
        public string Date { get; set; }

        [Required]
        [Display(Name = "Save to main inventory")]
        public bool Save { get; set; }


        
        [Display(Name = "Reason")]
        public string Reason { get; set; }




    }
}