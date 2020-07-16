using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Web;

namespace ims_mat_pro
{
    public class Program
    {
        public static string delivery_option = "-";
        public static decimal delivery_charges = 0;

     
        public static string conStr = @"data source=" + sqlServer + ";initial catalog=saaj;persist security info=True;user id=sa;password=gtR#1433;";
        public static string sqlUer = "sa";
        public static string sqlPw = "abc123";
        public static string sqlDB = "efs";
        public static string sqlServer = @"FASTECH";


        public static int small = 1;
        public static int medium = 2;
        public static int large = 2;
        public static int xLarge = 1;

        public static void createTable(string script)
        {
            StringBuilder sb = new StringBuilder();
            sb.AppendLine(script);
            string connectionStr = Program.conStr;
            SqlConnection conn = new SqlConnection(connectionStr);
            SqlCommand command = new SqlCommand();
            command = new SqlCommand(sb.ToString(), conn);
            try
            {
                conn.Open();
                command.ExecuteNonQuery();
                //KryptonMessageBox.Show("Database updated successfully!", "FASTECH", MessageBoxButtons.OK, MessageBoxIcon.Asterisk, MessageBoxDefaultButton.Button1);
            }
            catch (Exception ex)
            {
            }
            finally
            {
                try
                {
                    conn.Close();
                }
                catch (Exception ex)
                {
                    //KryptonMessageBox.Show("An error occurred while trying to close the database connection:" + ex, "Error", MessageBoxButtons.OK, MessageBoxIcon.Hand, MessageBoxDefaultButton.Button1);
                }
            }
        }

    }
}