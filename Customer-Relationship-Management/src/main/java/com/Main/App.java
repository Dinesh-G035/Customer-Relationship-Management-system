package com.Main;

import java.util.Scanner;
import com.service.CRMService;

public class App {
   public App() {
   }

   public static void main(String[] var0) {
      Scanner var1 = new Scanner(System.in);
      CRMService var2 = new CRMService();
      boolean var3 = true;

      while(var3) {
         System.out.println("\n=== CRM System Menu ===");
         System.out.println("1. Login");
         System.out.println("2. Sign Up");
         System.out.println("3. Exit");
         System.out.print("Select an option: ");
         int var4 = var1.nextInt();
         var1.nextLine();
         switch (var4) {
            case 1:
               var2.login(var1);
               break;
            case 2:
               var2.signup(var1);
               break;
            case 3:
               var3 = false;
               break;
            default:
               System.out.println("Invalid option.");
         }
      }

      var1.close();
   }
}
