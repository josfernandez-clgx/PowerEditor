using System;
using System.Collections.Generic;
using System.Text;
using System.Web.Services.Protocols;
using System.Net;
using System.IO;

namespace PEWSClient
{
    class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("About to call the web service");
            localhost.PowerEditorAPIInterfaceService CallWebService = new localhost.PowerEditorAPIInterfaceService();
            try
            {
                PEWSClient.localhost.powerEditorInterfaceReturnStructure pingResult = CallWebService.ping();
                Console.WriteLine("Result content: {0}", pingResult.content);

                String xmlContents = readFile("C:\\testdata\\ImportEntityTest-5-0.xml");
                PEWSClient.localhost.powerEditorInterfaceReturnStructure importResult = CallWebService.importEntitiesWithCredentials(xmlContents, true, "demo", "demo");
                Console.WriteLine("importResult is {0}\n", importResult.content);

                Console.WriteLine("General Messages");
                Console.WriteLine("----------------");
                string[] gms = importResult.generalMessages;
                for (int i = 0; i < gms.Length; i++)
                {
                    Console.WriteLine((i + 1) + ": " + gms[i] + "\n");
                }
                if (importResult.errorFlag == true)
                {
                    Console.WriteLine("Error Messages");
                    Console.WriteLine("--------------");
                    string [] ems = importResult.errorMessages;
                    for (int i = 0; i < ems.Length; i++) {
                        Console.WriteLine((i+1) + ": " + ems[i] + "\n");
                    }
                }
            }
            catch (SoapHeaderException ex)
            {
                Console.WriteLine("SoapHeaderException was hit.  Credentials are needed. \n{0}", ex.ToString());
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception: {0}", e.ToString());
            }

        }

        static String readFile(String filename)
        {
            StringBuilder sb = new StringBuilder();
            StringWriter sw = new StringWriter(sb);
            Stream strm = File.OpenRead(filename);
            StreamReader strrdr = new StreamReader(strm);
            sw.Write(strrdr.ReadToEnd());
            String retValue = sw.ToString();
            return retValue;
        }
    }
}
