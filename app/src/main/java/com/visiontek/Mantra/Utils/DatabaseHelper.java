package com.visiontek.Mantra.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PDS.db";

    //------------------------------DEALER TABLE
    private static final String DEALERS_TABLE_NAME = "Dealers_Table";

    private static final String DEALERS_AUTH_TYPE = "Dealers_Auth_Type";
    private static final String DEALERS_FUSION = "Dealers_Fusion";
    private static final String DEALERS_TYPE = "Dealers_Type";
    private static final String DEALERS_NAMES = "Dealers_Names";
    private static final String DEALERS_NAMES_LL = "Dealers_Names_Ll";
    private static final String DEALERS_UIDS = "Dealers_Uids";
    private static final String DEALERS_WADH = "Dealers_Wadh";

    //------------------------------MEMBERS TABLE
    private static final String MEMBERS_TABLE_NAME = "Members_Table";

    private static final String MEMBER_NAMES = "Member_Names";
    private static final String MEMBER_NAMESLL = "Member_Namesll";
    private static final String MEMBER_FUSION = "Member_Fusion";
    private static final String MEMBER_UIDS = "Member_Uids";
    private static final String MEMBER_WADH = "Member_Wadh";
    private static final String MEMBER_XFINGER = "Member_Xfinger";
    private static final String MEMBER_YIRIS = "Member_Yiris";
    private static final String MEMBER_ZMANUAL = "Member_Zmanual";
    private static final String MEMBER_ZMEMBERID = "Member_Zmemberid";
    private static final String MEMBER_ZOTP = "Member_Zotp";
    private static final String MEMBER_WADHAUTH = "Member_Wadhauth";

    //------------------------------RATION TABLE
    private static final String RATION_TABLE_NAME = "Ration_Table";
    private static final String TYPE = "Type";
    private static final String MONTH = "Month";
    private static final String YEAR = "Year";
    private static final String AVLQTY = "Avl";
    private static final String BALQTY = "Bal";
    private static final String CLOSEBAL = "Close";
    private static final String COMNAME = "Name";
    private static final String COMNAMELL = "Namell";
    private static final String COMCODE = "Code";
    private static final String MESUREUNIT = "Unit";
    private static final String MINQTY = "Min";
    private static final String PRICE = "Price";
    private static final String REQQTY = "Req";
    private static final String TOTQTY = "Tot";
    private static final String WEI = "Wei";

    //------------------------------PRINT TABLE
    private static final String PRINT_TABLE_NAME = "Print_Table";
    private static final String BAL = "bal_qty";
    private static final String CARRYOVER = "carry_over";
    private static final String INDIVIDUAL = "commIndividualAmount";
    private static final String CNAME = "comm_name";
    private static final String CNAMEL = "comm_name_ll";
    private static final String MNAME = "member_name";
    private static final String MNAMEL = "member_name_ll";
    private static final String RECIEPT = "reciept_id";
    private static final String RETAIL = "retail_price";
    private static final String PSCHEME = "scheme_desc_en";
    private static final String PSCHEMEL = "scheme_desc_ll";
    private static final String TOT = "tot_amount";
    private static final String TOTQ = "total_quantity";
    private static final String TTIME = "transaction_time";
    private static final String UIDREF = "uid_refer_no";

    //------------------------------MENU
    private static final String MENU_TABLE = "Menu_table";
    private static final String MAINMENU = "MainMenu";
    private static final String MENUNAME = "MenuName";
    private static final String SLNO = "Slno";
    private static final String STATUS = "Status";


    //------------------------------STOCK TABLE
    private static final String STOCK_TABLE_NAME = "Stock_Table";
    private static final String CB = "Cb";
    private static final String COMM = "Name";
    private static final String ISSUED = "Issue";
    private static final String STOCK = "Stock";
    private static final String SCHEME = "Shm";

    //------------------------------SALES TABLE
    private static final String SALES_TABLE_NAME = "Sales_Table";
    private static final String COMM_NAME = "Name";
    private static final String SALE = "Sale";
    private static final String SCHEMENAME = "Scheme";
    private static final String CARD = "Card";


    //------------------------------LANGUAGE TABLE
    private static final String LANGUAGE = "LANGUAGE";
    private static final String LANG_ = "LANG_";
    private static final String LANG = "LANG";


    //-------------------------------UIDSEEDING
    private static final String UIDSEEDING_TABLE_NAME = "UidSeeding_Table";

    private static final String UIDSEEDING_BFD_1 = "Uidseeding_Bfd_1";
    private static final String UIDSEEDING_BFD_2 = "Uidseeding_Bfd_2";
    private static final String UIDSEEDING_BFD_3 = "Uidseeding_Bfd_3";
    private static final String UIDSEEDING_MEMBERID = "Uidseeding_MemberId";
    private static final String UIDSEEDING_MEMBERNAME = "Uidseeding_MemberName";
    private static final String UIDSEEDING_MEMBERNAMELL = "Uidseeding_MemberNamell";
    private static final String UIDSEEDING_MEMBER_FUSION = "Uidseeding_MemberFusion";
    private static final String UIDSEEDING_UID = "Uidseeding_Uid";
    private static final String UIDSEEDING_W_UID_STATUS = "Uidseeding_W_Uid_Status";

    //-------------------------------BEN VERIFICATION
    private static final String BENEFICIARY_TABLE_NAME = "Beneficiary_Table";

    private static final String BENEFICIARY_MEMBERID = "Beneficiary_MemberId";
    private static final String BENEFICIARY_MEMEBERNAME = "Beneficiary_MemberName";
    private static final String BENEFICIARY_MEMBERNAMELL = "Beneficiary_MemberNamell";
    private static final String BENEFICIARY_MEMBERFUSION = "Beneficiary_Member_fusion";
    private static final String BENEFICIARY_UID = "Beneficiary_Uid";
    private static final String BENEFICIARY_VERIFICATION = "Beneficiary_Verification";
    private static final String BENEFICIARY_VERIFYSTATUSEN = "Beneficiary_VerifyStatus_en";
    private static final String BENEFICIARY_VERIFYSTATUSLL = "Beneficiary_VerifyStatus_ll";
    private static final String BENEFICIARY_W_UID_STATUS = "Beneficiary_W_Uid_Status";

    //-------------------------------INSPECTION
    private static final String INSPECTION_TABLE_NAME = "Inspection_Table";

    private static final String INSPECTION_CB = "Inspection_CB";
    private static final String INSPECTION_COMMCODE = "Inspection_CommCode";
    private static final String INSPECTION_COMMNAME = "Inspection_CommName";
    private static final String INSPECTION_COMMNAMELL = "Inspection_CommNamell";

    //-------------------------------INSPECTION APP
    private static final String INSPECTION_APP_TABLE_NAME = "Inspection_App_Table";

    private static final String INSPECTION_APP_KEY = "Inspection_App_Key";
    private static final String INSPECTION_APP_VALUE = "Inspection_App_Value";

    //-------------------------------RECIVE GOODS TRUCK

    private static final String RECIVEGOODS_TRUCK_TABLE_NAME = "Recivegoods_Truck_Table";

    private static final String RECIVEGOODS_TRUCK_FPSID = "Recivegoods_fpsId";
    private static final String RECIVEGOODS_TRUCK_AMONTH = "Recivegoods_allotedMonth";
    private static final String RECIVEGOODS_TRUCK_AYEAR = "Recivegoods_allotedYear";
    private static final String RECIVEGOODS_TRUCK_CHITNO = "Recivegoods_truckChitNo";
    private static final String RECIVEGOODS_TRUCK_CHALANID = "Recivegoods_challanId";
    private static final String RECIVEGOODS_TRUCK_AORDERNO = "Recivegoods_allocationOrderNo";
    private static final String RECIVEGOODS_TRUCK_TRUCKNO = "Recivegoods_truckNo";
    private static final String RECIVEGOODS_TRUCK_LENGTH = "Recivegoods_Length";

    //-------------------------------RECIVE GOODS COMM

    private static final String RECIVEGOODS_COMM_TABLE_NAME = "Recivegoods_Comm_Table";

    private static final String RECIVEGOODS_COMM_RELEASEDQTY = "Recivegoods_releasedQuantity";
    private static final String RECIVEGOODS_COMM_ALLOTMENT = "Recivegoods_allotment";
    private static final String RECIVEGOODS_COMM_COMMCODE = "Recivegoods_commCode";
    private static final String RECIVEGOODS_COMM_COMMNAME = "Recivegoods_commName";
    private static final String RECIVEGOODS_COMM_SCHEMEID = "Recivegoods_schemeId";
    private static final String RECIVEGOODS_COMM_SCHEMENAME = "Recivegoods_schemeName";

    //-------------------------------CANCEL REASON
    private static final String CANCEL_REQUEST_TABLE_NAME = "Cancel_Request_Table";

    private static final String CANCEL_ID = "Cancel_ID";
    private static final String CANCEL_REASON = "Cancel_Reason";



    private SQLiteDatabase db = this.getReadableDatabase();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + DEALERS_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,Dealers_Auth_Type TEXT ,Dealers_Fusion TEXT," +
                "Dealers_Type TEXT, Dealers_Names TEXT,Dealers_Names_Ll TEXT, Dealers_Uids TEXT,Dealers_Wadh TEXT )");

        //-------------------------------ISSUE------------------------------------------------------------------------------------------

        db.execSQL("create table " + MEMBERS_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,Member_Names TEXT ,Member_Namesll TEXT," +
                "Member_Fusion TEXT,Member_Uids TEXT,Member_Wadh TEXT, Member_Xfinger TEXT,Member_Yiris TEXT,Member_Zmanual TEXT," +
                " Member_Zmemberid TEXT,Member_Zotp TEXT, Member_Wadhauth TEXT)");

        db.execSQL("create table " + RATION_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,Type TEXT ,Month TEXT,Year TEXT,Avl TEXT ,Bal TEXT,Close TEXT," +
                "Name TEXT,Namell TEXT, Code TEXT, Unit TEXT,Min TEXT,Price TEXT,Req TEXT,Tot TEXT,Wei TEXT)");

        db.execSQL("create table " + PRINT_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,bal_qty TEXT ,carry_over TEXT,commIndividualAmount TEXT," +
                "comm_name TEXT, comm_name_ll TEXT, member_name TEXT,member_name_ll TEXT,reciept_id TEXT,retail_price TEXT,scheme_desc_en TEXT," +
                "scheme_desc_ll TEXT,tot_amount TEXT,total_quantity TEXT,transaction_time TEXT,uid_refer_no TEXT)");

        db.execSQL("create table " + MENU_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,MainMenu TEXT,MenuName TEXT,Slno TEXT,Status TEXT)");

        //-----------------------------REPORTS-------------------------------------------------------------------------------------

        db.execSQL("create table " + STOCK_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,Cb TEXT,Name TEXT ,Shm TEXT,Stock TEXT," +
                "Issue TEXT)");

        db.execSQL("create table " + SALES_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,Name TEXT,Sale TEXT ,Scheme TEXT,Card TEXT)");

        //-----------------------------LANGUAGE--------------------------------------------------------------------------------------

        db.execSQL("create table " + LANGUAGE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,LANG_ TEXT,LANG TEXT)");

        //-----------------------------AADHAAR SERVICES------------------------------------------------------------------------------------

        db.execSQL("create table " + UIDSEEDING_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,Uidseeding_Bfd_1 TEXT ,Uidseeding_Bfd_2 TEXT," +
                "Uidseeding_Bfd_3 TEXT, Uidseeding_MemberId TEXT,Uidseeding_MemberName TEXT, Uidseeding_MemberNamell TEXT,Uidseeding_MemberFusion TEXT,Uidseeding_Uid TEXT,Uidseeding_W_Uid_Status TEXT )");

        db.execSQL("create table " + BENEFICIARY_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,Beneficiary_MemberId TEXT ,Beneficiary_MemberName TEXT," +
                "Beneficiary_MemberNamell TEXT, Beneficiary_Member_fusion TEXT,Beneficiary_Uid TEXT, Beneficiary_Verification TEXT,Beneficiary_VerifyStatus_en TEXT,Beneficiary_VerifyStatus_ll TEXT,Beneficiary_W_Uid_Status TEXT )");

        //----------------------------INSPECTION-----------------------------------------------------------------------------------
        db.execSQL("create table " + INSPECTION_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,Inspection_CB TEXT,Inspection_CommCode TEXT ,Inspection_CommName TEXT,Inspection_CommNamell TEXT)");

        db.execSQL("create table " + INSPECTION_APP_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,Inspection_App_Key TEXT,Inspection_App_Value TEXT )");

        //----------------------------RECIVEGOODS
        db.execSQL("create table " + RECIVEGOODS_TRUCK_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,Recivegoods_fpsId TEXT,Recivegoods_allotedMonth TEXT " +
                ",Recivegoods_allotedYear TEXT,Recivegoods_truckChitNo TEXT,Recivegoods_challanId TEXT, Recivegoods_allocationOrderNo TEXT," +
                "Recivegoods_truckNo TEXT,Recivegoods_Length TEXT)");

        db.execSQL("create table " + RECIVEGOODS_COMM_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,Recivegoods_releasedQuantity TEXT,Recivegoods_allotment TEXT " +
                ",Recivegoods_commCode TEXT,Recivegoods_commName TEXT,Recivegoods_schemeId TEXT, Recivegoods_schemeName TEXT)");

        //-----------------------------CANCEL REQUEST
        db.execSQL("create table " + CANCEL_REQUEST_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,Cancel_ID TEXT,Cancel_Reason TEXT )");



        db.execSQL("CREATE TABLE IF NOT EXISTS PartialOnlineData(OffPassword text,OfflineLogin text,OfflineTxnTime text,Duration text,leftOfflineTime text,lastlogindate text,lastlogintime text,lastlogoutdate text,lastlogouttime text,AllotMonth text,AllotYear text,pOfflineStoppedDate text)");


        db.execSQL("CREATE TABLE IF NOT EXISTS KeyRegister(rcId text,commNameEn text,commNameLl text,commCode text,totalEntitlement text,balanceEntitlement text,schemeId text,schemeName text,commPrice text,Unit text,memberNameLl text,memberNameEn text,AllotMonth text,AllotYear text,allocationType text,allotedMonth text,allotedYear text)");


        db.execSQL("CREATE TABLE IF NOT EXISTS Pos_Ob(commCode text,commNameEn text,commNameLl text,closingBalance text)");


        db.execSQL("CREATE TABLE IF NOT EXISTS commodityMaster(commCode text,commNameEn text,commNameLl text,measurmentUnit text,commonCommCode text)");


        db.execSQL("CREATE TABLE IF NOT EXISTS schemeMaster(schemeId text,schemeName text)");


        db.execSQL("CREATE TABLE IF NOT EXISTS BenfiaryTxn(FpsId text,RcId text,SchemeId text,CommCode text,TotQty text,BalQty text,IssuedQty text,Rate text,commAmount text,TotAmt text,RecptId text,MemberName text,DateTime text,TxnUploadSts text,TxnType text,AllotMonth text, AllotYear text,allocationType text)");


        db.execSQL("CREATE TABLE IF NOT EXISTS OfflineStockReceive(TruckChitNum text,Challan text,VechNum text,FpsId text,SchemeId text,CommCode text,RecvdQty text,Unit text,Month text,Year text,DateTime text,RecvdUploadSts text,RecvdMode text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DEALERS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MEMBERS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RATION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PRINT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MENU_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + STOCK_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SALES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UIDSEEDING_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BENEFICIARY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + INSPECTION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + INSPECTION_APP_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RECIVEGOODS_TRUCK_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RECIVEGOODS_COMM_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CANCEL_REQUEST_TABLE_NAME);



        db.execSQL("DROP TABLE IF EXISTS PartialOnlineData");
        db.execSQL("DROP TABLE IF EXISTS KeyRegister");
        db.execSQL("DROP TABLE IF EXISTS Pos_Ob");
        db.execSQL("DROP TABLE IF EXISTS commodityMaster");
        db.execSQL("DROP TABLE IF EXISTS schemeMaster");
        db.execSQL("DROP TABLE IF EXISTS BenfiaryTxn");
        db.execSQL("DROP TABLE IF EXISTS OfflineStockReceive");
        onCreate(db);
    }


    public void deleteCancelRequest() {
        db = this.getWritableDatabase();
        db.execSQL("delete from " + CANCEL_REQUEST_TABLE_NAME);

        db.close();
    }
    public void deleterecivegoods_truck() {
        db = this.getWritableDatabase();
        db.execSQL("delete from " + RECIVEGOODS_TRUCK_TABLE_NAME);

        db.close();
    }

    public void deleterecivegoods_comm() {
        db = this.getWritableDatabase();
        db.execSQL("delete from " + RECIVEGOODS_COMM_TABLE_NAME);
        db.close();
    }

    public void deleteinspection_app() {
        db = this.getWritableDatabase();
        db.execSQL("delete from " + INSPECTION_APP_TABLE_NAME);
        db.close();
    }

    public void deleteinspection() {
        db = this.getWritableDatabase();
        db.execSQL("delete from " + INSPECTION_TABLE_NAME);
        db.close();
    }

    public void deletebenverification() {
        db = this.getWritableDatabase();
        db.execSQL("delete from " + BENEFICIARY_TABLE_NAME);
        db.close();
    }

    public void deleteuidseeding() {
        db = this.getWritableDatabase();
        db.execSQL("delete from " + UIDSEEDING_TABLE_NAME);
        db.close();
    }

    public void deletedealertable() {
        db = this.getWritableDatabase();
        db.execSQL("delete from " + DEALERS_TABLE_NAME);
        db.close();
    }

    public void deletemembertable() {
        db = this.getWritableDatabase();
        db.execSQL("delete from " + MEMBERS_TABLE_NAME);
        db.execSQL("delete from " + RATION_TABLE_NAME);
        db.close();
    }

    public void deleteprinttable() {
        db = this.getWritableDatabase();
        db.execSQL("delete from " + PRINT_TABLE_NAME);
        db.close();
    }

    public void deletestocktable() {
        db = this.getWritableDatabase();
        db.execSQL("delete from " + STOCK_TABLE_NAME);
        db.close();
    }

    public void deletesaletable() {
        db = this.getWritableDatabase();
        db.execSQL("delete from " + SALES_TABLE_NAME);
        db.close();
    }

    private void deletemenu() {
        db = this.getWritableDatabase();
        db.execSQL("delete from " + MENU_TABLE);
        db.close();
    }

    private void deleteDSD() {
        db = this.getWritableDatabase();
        db.execSQL("delete from " + SALES_TABLE_NAME);
        db.close();
    }

    private void deleteSD() {
        db = this.getWritableDatabase();
        db.execSQL("delete from " + STOCK_TABLE_NAME);
        db.close();
    }


    public void insert_L(String lang, String lng) {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LANG_, lang);
        cv.put(LANG, lng);
        long result = db.insert(LANGUAGE, null, cv);
        System.out.println("INSERTION RESULT = " + result);
        db.close();
    }

    public String get_L(String filename) {
        String st = "";
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.query(LANGUAGE, new String[]{LANG_, LANG}, LANG_ + "=?",
                new String[]{String.valueOf(filename)}, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            st = cursor.getString(cursor.getColumnIndex("LANG"));
            cursor.close();
            db.close();
            return st;
        } else {
            return st;
        }
    }

    public void update_L(String filename, String filedate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LANG_, filename);
        contentValues.put(LANG, filedate);
        db.update(LANGUAGE, contentValues, LANG_ + " = ?", new String[]{filename});
        db.close();
    }

    public int getfileCount() {
        String countQuery = "SELECT  * FROM " + LANGUAGE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void insert_Cancel_Request(ArrayList<String> approveKey, ArrayList<String> approveValue) {
        deleteCancelRequest();
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < approveValue.size(); i++) {
            cv.put(CANCEL_ID, approveKey.get(i));
            cv.put(CANCEL_REASON, approveValue.get(i));

            long result = db.insert(CANCEL_REQUEST_TABLE_NAME, null, cv);
            System.out.println("INSERTION RESULT = " + result);
        }
        db.close();
    }

    public void insert_RECIVED_COMM(ArrayList<String> releasedQuantity, ArrayList<String> allotment, ArrayList<String> commCode,
                                    ArrayList<String> commName, ArrayList<String> schemeId, ArrayList<String> schemeName) {

        deleterecivegoods_comm();
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < releasedQuantity.size(); i++) {
            cv.put(RECIVEGOODS_COMM_RELEASEDQTY, releasedQuantity.get(i));
            cv.put(RECIVEGOODS_COMM_ALLOTMENT, allotment.get(i));
            cv.put(RECIVEGOODS_COMM_COMMCODE, commCode.get(i));
            cv.put(RECIVEGOODS_COMM_COMMNAME, commName.get(i));
            cv.put(RECIVEGOODS_COMM_SCHEMEID, schemeId.get(i));
            cv.put(RECIVEGOODS_COMM_SCHEMENAME, schemeName.get(i));
            long result = db.insert(RECIVEGOODS_COMM_TABLE_NAME, null, cv);
            System.out.println("INSERTION RESULT = " + result);
        }
        db.close();
    }

    public void insert_RECIVED_TRUCK(ArrayList<String> fpsId, ArrayList<String> allotedMonth, ArrayList<String> allotedYear, ArrayList<String> truckChitNo,
                                     ArrayList<String> challanId, ArrayList<String> allocationOrderNo, ArrayList<String> truckNo, ArrayList<String> commlength) {
        deleterecivegoods_truck();
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < fpsId.size(); i++) {
            cv.put(RECIVEGOODS_TRUCK_FPSID, fpsId.get(i));
            cv.put(RECIVEGOODS_TRUCK_AMONTH, allotedMonth.get(i));
            cv.put(RECIVEGOODS_TRUCK_AYEAR, allotedYear.get(i));
            cv.put(RECIVEGOODS_TRUCK_CHITNO, truckChitNo.get(i));
            cv.put(RECIVEGOODS_TRUCK_CHALANID, challanId.get(i));
            cv.put(RECIVEGOODS_TRUCK_AORDERNO, allocationOrderNo.get(i));
            cv.put(RECIVEGOODS_TRUCK_TRUCKNO, truckNo.get(i));
            cv.put(RECIVEGOODS_TRUCK_LENGTH, commlength.get(i));

            long result = db.insert(RECIVEGOODS_TRUCK_TABLE_NAME, null, cv);
            System.out.println("INSERTION RESULT = " + result);
        }
        db.close();
    }


    public void insert_INSPECTION_app(ArrayList<String> approveKey, ArrayList<String> approveValue) {
        deleteinspection_app();
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < approveValue.size(); i++) {
            cv.put(INSPECTION_APP_KEY, approveKey.get(i));
            cv.put(INSPECTION_APP_VALUE, approveValue.get(i));

            long result = db.insert(INSPECTION_APP_TABLE_NAME, null, cv);
            System.out.println("INSERTION RESULT = " + result);
        }
        db.close();
    }

    public void insert_INSPECTION(ArrayList<String> closingBalance, ArrayList<String> commCode, ArrayList<String> commNameEn, ArrayList<String> commNamell) {
        deleteinspection();
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < commCode.size(); i++) {
            cv.put(INSPECTION_CB, closingBalance.get(i));
            cv.put(INSPECTION_COMMCODE, commCode.get(i));
            cv.put(INSPECTION_COMMNAME, commNameEn.get(i));
            cv.put(INSPECTION_COMMNAMELL, commNamell.get(i));

            long result = db.insert(INSPECTION_TABLE_NAME, null, cv);
            System.out.println("INSERTION RESULT = " + result);
        }
        db.close();
    }


    public void insert_BEN(ArrayList<String> memberId, ArrayList<String> memberName, ArrayList<String> memberNamell, ArrayList<String> member_fusion,
                           ArrayList<String> uid, ArrayList<String> verification, ArrayList<String> verifyStatus_en, ArrayList<String> verifyStatus_ll,
                           ArrayList<String> w_uid_status) {
        deletebenverification();
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < memberId.size(); i++) {
            cv.put(BENEFICIARY_MEMBERID, memberId.get(i));
            cv.put(BENEFICIARY_MEMEBERNAME, memberName.get(i));
            cv.put(BENEFICIARY_MEMBERNAMELL, memberNamell.get(i));
            cv.put(BENEFICIARY_MEMBERFUSION, member_fusion.get(i));
            cv.put(BENEFICIARY_UID, uid.get(i));
            cv.put(BENEFICIARY_VERIFICATION, verification.get(i));
            cv.put(BENEFICIARY_VERIFYSTATUSEN, verifyStatus_en.get(i));
            cv.put(BENEFICIARY_VERIFYSTATUSLL, verifyStatus_ll.get(i));
            cv.put(BENEFICIARY_W_UID_STATUS, w_uid_status.get(i));
            long result = db.insert(BENEFICIARY_TABLE_NAME, null, cv);
            System.out.println("INSERTION RESULT = " + result);
        }
        db.close();
    }

    public void insert_UID(ArrayList<String> bfd1, ArrayList<String> bfd2, ArrayList<String> bfd3, ArrayList<String> memberId,
                           ArrayList<String> memberName, ArrayList<String> memberNamell, ArrayList<String> member_fusion, ArrayList<String> uid,
                           ArrayList<String> w_uid_status) {
        deleteuidseeding();
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < bfd1.size(); i++) {
            cv.put(UIDSEEDING_BFD_1, bfd1.get(i));
            cv.put(UIDSEEDING_BFD_2, bfd2.get(i));
            cv.put(UIDSEEDING_BFD_3, bfd3.get(i));
            cv.put(UIDSEEDING_MEMBERID, memberId.get(i));
            cv.put(UIDSEEDING_MEMBERNAME, memberName.get(i));
            cv.put(UIDSEEDING_MEMBERNAMELL, memberNamell.get(i));
            cv.put(UIDSEEDING_MEMBER_FUSION, member_fusion.get(i));
            cv.put(UIDSEEDING_UID, uid.get(i));
            cv.put(UIDSEEDING_W_UID_STATUS, w_uid_status.get(i));
            long result = db.insert(UIDSEEDING_TABLE_NAME, null, cv);
            System.out.println("INSERTION RESULT = " + result);
        }
        db.close();
    }

    public void insert_DD(ArrayList<String> dauthtypes, ArrayList<String> dfusion, ArrayList<String> dtypes,
                          ArrayList<String> dnames, ArrayList<String> dnamell, ArrayList<String> duids, ArrayList<String> dwadh) {
        deletedealertable();
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < dauthtypes.size(); i++) {
            cv.put(DEALERS_AUTH_TYPE, dauthtypes.get(i));
            cv.put(DEALERS_FUSION, dfusion.get(i));
            cv.put(DEALERS_TYPE, dtypes.get(i));
            cv.put(DEALERS_NAMES, dnames.get(i));
            cv.put(DEALERS_NAMES_LL, dnamell.get(i));
            cv.put(DEALERS_UIDS, duids.get(i));
            cv.put(DEALERS_WADH, dwadh.get(i));
            long result = db.insert(DEALERS_TABLE_NAME, null, cv);
            System.out.println("INSERTION RESULT = " + result);
        }
        db.close();
    }

  /*  public void insert_DDD(ArrayList<String> ddetails) {
        deletedealerdetailstable();
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < ddetails.size(); i++) {
            cv.put(DEALER_DETAILS, ddetails.get(i));
            long result = db.insert(DEALERS_DETAILS_TABLE_NAME, null, cv);
            System.out.println("INSERTION RESULT = " + result);
        }
        db.close();
    }*/

    public void insert_MD(ArrayList<String> memberName, ArrayList<String> memberNamell, ArrayList<String> member_fusion,
                          ArrayList<String> uid, ArrayList<String> w_uid_status, ArrayList<String> xfinger, ArrayList<String> yiris,
                          ArrayList<String> zmanual, ArrayList<String> zmemberId, ArrayList<String> zotp, ArrayList<String> zwgenWadhAuth) {
        deletemembertable();
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < memberName.size(); i++) {
            cv.put(MEMBER_NAMES, memberName.get(i));
            System.out.println("MEMBER_NAMES=" + memberName.get(i));
            cv.put(MEMBER_NAMESLL, memberNamell.get(i));
            cv.put(MEMBER_FUSION, member_fusion.get(i));
            cv.put(MEMBER_UIDS, uid.get(i));
            cv.put(MEMBER_WADH, w_uid_status.get(i));
            cv.put(MEMBER_XFINGER, xfinger.get(i));
            cv.put(MEMBER_YIRIS, yiris.get(i));
            cv.put(MEMBER_ZMANUAL, zmanual.get(i));
            cv.put(MEMBER_ZMEMBERID, zmemberId.get(i));
            cv.put(MEMBER_ZOTP, zotp.get(i));
            cv.put(MEMBER_WADHAUTH, zwgenWadhAuth.get(i));
            long result = db.insert(MEMBERS_TABLE_NAME, null, cv);
            System.out.println("INSERTION RESULT = " + result);
        }
        db.close();
    }

    public void insert_RD(ArrayList<String> type, ArrayList<String> month, ArrayList<String> year,
                          ArrayList<String> avl, ArrayList<String> bal, ArrayList<String> close,
                          ArrayList<String> name, ArrayList<String> namell, ArrayList<String> code, ArrayList<String> unit,
                          ArrayList<String> min, ArrayList<String> price, ArrayList<String> req,
                          ArrayList<String> tot, ArrayList<String> wei) {
        db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        for (int i = 0; i < avl.size(); i++) {
            cv.put(TYPE, type.get(i));
            cv.put(MONTH, month.get(i));
            cv.put(YEAR, year.get(i));
            cv.put(AVLQTY, avl.get(i));
            System.out.println("AVLQTY=" + avl.get(i));
            cv.put(BALQTY, bal.get(i));
            cv.put(CLOSEBAL, close.get(i));
            cv.put(COMNAME, name.get(i));
            cv.put(COMNAMELL, namell.get(i));
            cv.put(COMCODE, code.get(i));
            cv.put(MESUREUNIT, unit.get(i));
            cv.put(MINQTY, min.get(i));
            cv.put(PRICE, price.get(i));
            cv.put(REQQTY, req.get(i));
            cv.put(TOTQTY, tot.get(i));
            cv.put(WEI, wei.get(i));
            db.insert(RATION_TABLE_NAME, null, cv);
        }
        db.close();
    }

    public void insert_PD(ArrayList<String> bal_qty, ArrayList<String> carry_over, ArrayList<String> commIndividualAmount,
                          ArrayList<String> comm_name, ArrayList<String> comm_name_ll, ArrayList<String> member_name,
                          ArrayList<String> member_name_ll, ArrayList<String> reciept_id, ArrayList<String> retail_price, ArrayList<String> scheme_desc_en,
                          ArrayList<String> scheme_desc_ll, ArrayList<String> tot_amount, ArrayList<String> total_quantity,
                          ArrayList<String> transaction_time, ArrayList<String> uid_refer_no) {
        deleteprinttable();
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        for (int i = 0; i < bal_qty.size(); i++) {
            cv.put(BAL, bal_qty.get(i));
            cv.put(CARRYOVER, carry_over.get(i));
            cv.put(INDIVIDUAL, commIndividualAmount.get(i));
            cv.put(CNAME, comm_name.get(i));
            System.out.println("BAL=" + bal_qty.get(i));
            cv.put(CNAMEL, comm_name_ll.get(i));
            cv.put(MNAME, member_name.get(i));
            cv.put(MNAMEL, member_name_ll.get(i));
            cv.put(RECIEPT, reciept_id.get(i));
            cv.put(RETAIL, retail_price.get(i));
            cv.put(PSCHEME, scheme_desc_en.get(i));
            cv.put(PSCHEMEL, scheme_desc_ll.get(i));
            cv.put(TOT, tot_amount.get(i));
            cv.put(TOTQ, total_quantity.get(i));
            cv.put(TTIME, transaction_time.get(i));
            cv.put(UIDREF, uid_refer_no.get(i));

            db.insert(PRINT_TABLE_NAME, null, cv);
        }

        db.close();
    }

    public void insert_MENU(ArrayList<String> mainmenu, ArrayList<String> menuname, ArrayList<String> salno, ArrayList<String> status) {

        deletemenu();
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < mainmenu.size(); i++) {
            cv.put(MAINMENU, mainmenu.get(i));
            System.out.println("MEMBER_NAMES=" + mainmenu.get(i));
            cv.put(MENUNAME, menuname.get(i));
            cv.put(SLNO, salno.get(i));
            cv.put(STATUS, status.get(i));
            long result = db.insert(MENU_TABLE, null, cv);
            System.out.println("INSERTION RESULT = " + result);
        }
        db.close();
    }

    public void insert_SD(ArrayList<String> stock) {
        deleteSD();
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CB, stock.get(0));
        cv.put(COMM, stock.get(1));
        System.out.println("STOCK=" + stock.get(1));
        cv.put(ISSUED, stock.get(2));
        cv.put(STOCK, stock.get(3));
        cv.put(SCHEME, stock.get(4));
        long result = db.insert(STOCK_TABLE_NAME, null, cv);
        System.out.println("INSERTION RESULT = " + result);
        db.close();
    }

    public void insert_DSD(ArrayList<String> sale) {
        deleteDSD();
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COMM_NAME, sale.get(0));
        cv.put(SALE, sale.get(1));
        System.out.println("STOCK=" + sale.get(1));
        cv.put(SCHEMENAME, sale.get(2));
        cv.put(CARD, sale.get(3));

        long result = db.insert(SALES_TABLE_NAME, null, cv);
        System.out.println("INSERTION RESULT = " + result);
        db.close();
    }

    public ArrayList<String> get_DD(int get) {
        ArrayList<String> array_list = new ArrayList<>();
        db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor res = db.rawQuery("select * from Dealers_Table", null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            switch (get) {
                case 0:
                    array_list.add(res.getString(res.getColumnIndex(DEALERS_AUTH_TYPE)));
                    break;
                case 1:
                    array_list.add(res.getString(res.getColumnIndex(DEALERS_TYPE)));
                    break;
                case 2:
                    array_list.add(res.getString(res.getColumnIndex(DEALERS_NAMES)));
                    break;
                case 3:
                    array_list.add(res.getString(res.getColumnIndex(DEALERS_UIDS)));
                    break;
                case 4:
                    array_list.add(res.getString(res.getColumnIndex(DEALERS_FUSION)));
                    break;
                case 5:
                    array_list.add(res.getString(res.getColumnIndex(DEALERS_NAMES_LL)));
                    break;
                case 6:
                    array_list.add(res.getString(res.getColumnIndex(DEALERS_WADH)));
                    break;
            }
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> get_MD(int get) {
        ArrayList<String> array_list = new ArrayList<>();
        db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor res = db.rawQuery("select * from Members_Table", null);
        res.moveToFirst();
        System.out.println("COUNT" + res.getCount());
        while (!res.isAfterLast()) {
            switch (get) {
                case 0:
                    array_list.add(res.getString(res.getColumnIndex(MEMBER_NAMES)));
                    break;
                case 1:
                    array_list.add(res.getString(res.getColumnIndex(MEMBER_UIDS)));
                    break;
                case 2:
                    array_list.add(res.getString(res.getColumnIndex(MEMBER_XFINGER)));
                    break;
                case 3:
                    array_list.add(res.getString(res.getColumnIndex(MEMBER_YIRIS)));
                    break;
                case 4:
                    array_list.add(res.getString(res.getColumnIndex(MEMBER_ZMEMBERID)));
                    break;
                case 5:
                    array_list.add(res.getString(res.getColumnIndex(MEMBER_NAMESLL)));
                    break;
                case 6:
                    array_list.add(res.getString(res.getColumnIndex(MEMBER_FUSION)));
                    break;
                case 7:
                    array_list.add(res.getString(res.getColumnIndex(MEMBER_WADH)));
                    break;
                case 8:
                    array_list.add(res.getString(res.getColumnIndex(MEMBER_ZMANUAL)));
                    break;
                case 9:
                    array_list.add(res.getString(res.getColumnIndex(MEMBER_ZOTP)));
                    break;
                case 10:
                    array_list.add(res.getString(res.getColumnIndex(MEMBER_WADHAUTH)));
                    break;
            }
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> get_RD(int get) {
        ArrayList<String> array_list = new ArrayList<>();
        //
        @SuppressLint("Recycle")
        Cursor res = db.rawQuery("select * from Ration_Table", null);
        res.moveToFirst();
        System.out.println("COUNT" + res.getCount());
        while (!res.isAfterLast()) {
            switch (get) {
                case 0:
                    array_list.add(res.getString(res.getColumnIndex(AVLQTY)));
                    break;
                case 1:
                    array_list.add(res.getString(res.getColumnIndex(BALQTY)));
                    break;
                case 2:
                    array_list.add(res.getString(res.getColumnIndex(CLOSEBAL)));
                    break;
                case 3:
                    array_list.add(res.getString(res.getColumnIndex(COMNAME)));
                    break;
                case 4:
                    array_list.add(res.getString(res.getColumnIndex(COMCODE)));
                    break;
                case 5:
                    array_list.add(res.getString(res.getColumnIndex(MESUREUNIT)));
                    break;
                case 6:
                    array_list.add(res.getString(res.getColumnIndex(MINQTY)));
                    break;
                case 7:
                    array_list.add(res.getString(res.getColumnIndex(PRICE)));
                    break;
                case 8:
                    array_list.add(res.getString(res.getColumnIndex(REQQTY)));
                    break;
                case 9:
                    array_list.add(res.getString(res.getColumnIndex(TOTQTY)));
                    break;
                case 10:
                    array_list.add(res.getString(res.getColumnIndex(WEI)));
                    break;
                case 11:
                    array_list.add(res.getString(res.getColumnIndex(TYPE)));
                    break;
                case 12:
                    array_list.add(res.getString(res.getColumnIndex(MONTH)));
                    break;
                case 13:
                    array_list.add(res.getString(res.getColumnIndex(YEAR)));
                    break;
                case 14:
                    array_list.add(res.getString(res.getColumnIndex(COMNAMELL)));
                    break;
            }
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> get_PD(int get) {
        ArrayList<String> array_list = new ArrayList<>();
        //
        @SuppressLint("Recycle")
        Cursor res = db.rawQuery("select * from Print_Table", null);
        res.moveToFirst();
        System.out.println("COUNT" + res.getCount());
        while (!res.isAfterLast()) {
            switch (get) {
                case 0:
                    array_list.add(res.getString(res.getColumnIndex(BAL)));
                    break;
                case 1:
                    array_list.add(res.getString(res.getColumnIndex(CARRYOVER)));
                    break;
                case 2:
                    array_list.add(res.getString(res.getColumnIndex(INDIVIDUAL)));
                    break;
                case 3:
                    array_list.add(res.getString(res.getColumnIndex(CNAME)));
                    break;
                case 4:
                    array_list.add(res.getString(res.getColumnIndex(CNAMEL)));
                    break;
                case 5:
                    array_list.add(res.getString(res.getColumnIndex(MNAME)));
                    break;
                case 6:
                    array_list.add(res.getString(res.getColumnIndex(MNAMEL)));
                    break;
                case 7:
                    array_list.add(res.getString(res.getColumnIndex(RECIEPT)));
                    break;
                case 8:
                    array_list.add(res.getString(res.getColumnIndex(RETAIL)));
                    break;
                case 9:
                    array_list.add(res.getString(res.getColumnIndex(PSCHEME)));
                    break;
                case 10:
                    array_list.add(res.getString(res.getColumnIndex(PSCHEMEL)));
                    break;
                case 11:
                    array_list.add(res.getString(res.getColumnIndex(TOT)));
                    break;
                case 12:
                    array_list.add(res.getString(res.getColumnIndex(TOTQ)));
                    break;
                case 13:
                    array_list.add(res.getString(res.getColumnIndex(TTIME)));
                    break;
                case 14:
                    array_list.add(res.getString(res.getColumnIndex(UIDREF)));
                    break;
            }
            res.moveToNext();
        }
        return array_list;
    }

   /* public ArrayList<String> get_PD(int get) {
        ArrayList<String> array_list = new ArrayList<>();

        @SuppressLint("Recycle")
        Cursor res = db.rawQuery("select * from Print_Table", null);
        res.moveToFirst();
        System.out.println("COUNT" + res.getCount());
        while (!res.isAfterLast()) {
            switch (get) {
                case 0:
                    array_list.add(res.getString(res.getColumnIndex(BAL)));
                    break;
                case 1:
                    array_list.add(res.getString(res.getColumnIndex(CARRYOVER)));
                    break;
                case 2:
                    array_list.add(res.getString(res.getColumnIndex(INDIVIDUAL)));
                    break;
                case 3:
                    array_list.add(res.getString(res.getColumnIndex(CNAME)));
                    break;
                case 4:
                    array_list.add(res.getString(res.getColumnIndex(MNAME)));
                    break;
                case 5:
                    array_list.add(res.getString(res.getColumnIndex(RECIEPT)));
                    break;
                case 6:
                    array_list.add(res.getString(res.getColumnIndex(RETAIL)));
                    break;
                case 7:
                    array_list.add(res.getString(res.getColumnIndex(TOT)));
                    break;
                case 8:
                    array_list.add(res.getString(res.getColumnIndex(TOTQ)));
                    break;
                case 9:
                    array_list.add(res.getString(res.getColumnIndex(TTIME)));
                    break;

            }
            res.moveToNext();
        }
        return array_list;
    }*/

    public ArrayList<String> get_MENU(int get) {
        ArrayList<String> array_list = new ArrayList<>();
        db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor res = db.rawQuery("select * from Menu_Table", null);
        res.moveToFirst();
        System.out.println("COUNT" + res.getCount());
        while (!res.isAfterLast()) {
            switch (get) {
                case 0:
                    array_list.add(res.getString(res.getColumnIndex(MAINMENU)));
                    break;
                case 1:
                    array_list.add(res.getString(res.getColumnIndex(MENUNAME)));
                    break;
                case 2:
                    array_list.add(res.getString(res.getColumnIndex(SLNO)));
                    break;
                case 3:
                    array_list.add(res.getString(res.getColumnIndex(STATUS)));
                    break;
            }
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<Integer> get_MENU_serial(int get) {

        ArrayList<Integer> int_array_list = new ArrayList<>();
        db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor res = db.rawQuery("select * from Menu_Table", null);
        res.moveToFirst();
        System.out.println("COUNT" + res.getCount());
        while (!res.isAfterLast()) {
            if (get == 1) {
                int_array_list.add(Integer.valueOf(res.getString(res.getColumnIndex(SLNO))));
            }
            res.moveToNext();
        }
        return int_array_list;
    }


    public ArrayList<String> get_SD(int get) {
        ArrayList<String> array_list = new ArrayList<>();
        db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor res = db.rawQuery("select * from STOCK_TABLE", null);
        res.moveToFirst();
        System.out.println("COUNT" + res.getCount());
        while (!res.isAfterLast()) {
            switch (get) {
                case 0:
                    array_list.add(res.getString(res.getColumnIndex(CB)));
                    break;
                case 1:
                    array_list.add(res.getString(res.getColumnIndex(COMM)));
                    break;
                case 2:
                    array_list.add(res.getString(res.getColumnIndex(ISSUED)));
                    break;
                case 3:
                    array_list.add(res.getString(res.getColumnIndex(STOCK)));
                    break;
                case 4:
                    array_list.add(res.getString(res.getColumnIndex(SCHEME)));
                    break;
            }
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> get_DSD(int get) {
        ArrayList<String> array_list = new ArrayList<>();
        db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor res = db.rawQuery("select * from Sales_TABLE", null);
        res.moveToFirst();
        System.out.println("COUNT" + res.getCount());
        while (!res.isAfterLast()) {
            switch (get) {
                case 0:
                    array_list.add(res.getString(res.getColumnIndex(COMM_NAME)));
                    break;
                case 1:
                    array_list.add(res.getString(res.getColumnIndex(SALE)));
                    break;
                case 2:
                    array_list.add(res.getString(res.getColumnIndex(SCHEMENAME)));
                    break;
                case 3:
                    array_list.add(res.getString(res.getColumnIndex(CARD)));
                    break;
            }
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> get_UID(int get) {
        ArrayList<String> array_list = new ArrayList<>();

        @SuppressLint("Recycle")
        Cursor res = db.rawQuery("select * from UidSeeding_Table", null);
        res.moveToFirst();
        System.out.println("COUNT" + res.getCount());
        while (!res.isAfterLast()) {
            switch (get) {
                case 0:
                    array_list.add(res.getString(res.getColumnIndex(UIDSEEDING_BFD_1)));
                    break;
                case 1:
                    array_list.add(res.getString(res.getColumnIndex(UIDSEEDING_BFD_2)));
                    break;
                case 2:
                    array_list.add(res.getString(res.getColumnIndex(UIDSEEDING_BFD_3)));
                    break;
                case 3:
                    array_list.add(res.getString(res.getColumnIndex(UIDSEEDING_MEMBERID)));
                    break;
                case 4:
                    array_list.add(res.getString(res.getColumnIndex(UIDSEEDING_MEMBERNAME)));
                    break;
                case 5:
                    array_list.add(res.getString(res.getColumnIndex(UIDSEEDING_MEMBERNAMELL)));
                    break;
                case 6:
                    array_list.add(res.getString(res.getColumnIndex(UIDSEEDING_MEMBER_FUSION)));
                    break;
                case 7:
                    array_list.add(res.getString(res.getColumnIndex(UIDSEEDING_UID)));
                    break;
                case 8:
                    array_list.add(res.getString(res.getColumnIndex(UIDSEEDING_W_UID_STATUS)));
                    break;

            }
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> get_BEN(int get) {
        ArrayList<String> array_list = new ArrayList<>();
        @SuppressLint("Recycle")
        Cursor res = db.rawQuery("select * from Beneficiary_Table", null);
        res.moveToFirst();
        System.out.println("COUNT" + res.getCount());
        while (!res.isAfterLast()) {
            switch (get) {
                case 0:
                    array_list.add(res.getString(res.getColumnIndex(BENEFICIARY_MEMBERID)));
                    break;
                case 1:
                    array_list.add(res.getString(res.getColumnIndex(BENEFICIARY_MEMEBERNAME)));
                    break;
                case 2:
                    array_list.add(res.getString(res.getColumnIndex(BENEFICIARY_MEMBERNAMELL)));
                    break;
                case 3:
                    array_list.add(res.getString(res.getColumnIndex(BENEFICIARY_MEMBERFUSION)));
                    break;
                case 4:
                    array_list.add(res.getString(res.getColumnIndex(BENEFICIARY_UID)));
                    break;
                case 5:
                    array_list.add(res.getString(res.getColumnIndex(BENEFICIARY_VERIFICATION)));
                    break;
                case 6:
                    array_list.add(res.getString(res.getColumnIndex(BENEFICIARY_VERIFYSTATUSEN)));
                    break;
                case 7:
                    array_list.add(res.getString(res.getColumnIndex(BENEFICIARY_VERIFYSTATUSLL)));
                    break;
                case 8:
                    array_list.add(res.getString(res.getColumnIndex(BENEFICIARY_W_UID_STATUS)));
                    break;

            }
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> get_INSPECTION(int get) {
        ArrayList<String> array_list = new ArrayList<>();
        db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor res = db.rawQuery("select * from Inspection_Table", null);
        res.moveToFirst();
        System.out.println("COUNT" + res.getCount());
        while (!res.isAfterLast()) {
            switch (get) {
                case 0:
                    array_list.add(res.getString(res.getColumnIndex(INSPECTION_CB)));
                    break;
                case 1:
                    array_list.add(res.getString(res.getColumnIndex(INSPECTION_COMMCODE)));
                    break;
                case 2:
                    array_list.add(res.getString(res.getColumnIndex(INSPECTION_COMMNAME)));
                    break;
                case 3:
                    array_list.add(res.getString(res.getColumnIndex(INSPECTION_COMMNAMELL)));
                    break;
            }
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> get_APP(int get) {
        ArrayList<String> array_list = new ArrayList<>();
        db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor res = db.rawQuery("select * from Inspection_App_Table ", null);
        res.moveToFirst();
        System.out.println("COUNT" + res.getCount());
        while (!res.isAfterLast()) {
            switch (get) {
                case 0:
                    array_list.add(res.getString(res.getColumnIndex(INSPECTION_APP_KEY)));
                    break;
                case 1:
                    array_list.add(res.getString(res.getColumnIndex(INSPECTION_APP_VALUE)));
                    break;
            }
            res.moveToNext();
        }
        return array_list;
    }


    public ArrayList<String> get_RC_Truck(int get) {
        ArrayList<String> array_list = new ArrayList<>();
        db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor res = db.rawQuery("select * from Recivegoods_Truck_Table", null);
        res.moveToFirst();
        System.out.println("COUNT" + res.getCount());
        while (!res.isAfterLast()) {
            switch (get) {
                case 0:
                    array_list.add(res.getString(res.getColumnIndex(RECIVEGOODS_TRUCK_FPSID)));
                    break;
                case 1:
                    array_list.add(res.getString(res.getColumnIndex(RECIVEGOODS_TRUCK_AMONTH)));
                    break;
                case 2:
                    array_list.add(res.getString(res.getColumnIndex(RECIVEGOODS_TRUCK_AYEAR)));
                    break;
                case 3:
                    array_list.add(res.getString(res.getColumnIndex(RECIVEGOODS_TRUCK_CHITNO)));
                    break;
                case 4:
                    array_list.add(res.getString(res.getColumnIndex(RECIVEGOODS_TRUCK_CHALANID)));
                    break;
                case 5:
                    array_list.add(res.getString(res.getColumnIndex(RECIVEGOODS_TRUCK_AORDERNO)));
                    break;
                case 6:
                    array_list.add(res.getString(res.getColumnIndex(RECIVEGOODS_TRUCK_TRUCKNO)));
                    break;
                case 7:
                    array_list.add(res.getString(res.getColumnIndex(RECIVEGOODS_TRUCK_LENGTH)));
                    break;
            }
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> get_RC_Comm(int get) {
        ArrayList<String> array_list = new ArrayList<>();
        db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor res = db.rawQuery("select * from Recivegoods_Comm_Table", null);
        res.moveToFirst();
        System.out.println("COUNT" + res.getCount());
        while (!res.isAfterLast()) {
            switch (get) {
                case 0:
                    array_list.add(res.getString(res.getColumnIndex(RECIVEGOODS_COMM_RELEASEDQTY)));
                    break;
                case 1:
                    array_list.add(res.getString(res.getColumnIndex(RECIVEGOODS_COMM_ALLOTMENT)));
                    break;
                case 2:
                    array_list.add(res.getString(res.getColumnIndex(RECIVEGOODS_COMM_COMMCODE)));
                    break;
                case 3:
                    array_list.add(res.getString(res.getColumnIndex(RECIVEGOODS_COMM_COMMNAME)));
                    break;
                case 4:
                    array_list.add(res.getString(res.getColumnIndex(RECIVEGOODS_COMM_SCHEMEID)));
                    break;
                case 5:
                    array_list.add(res.getString(res.getColumnIndex(RECIVEGOODS_COMM_SCHEMENAME)));
                    break;
            }
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> get_CancelRequest(int get) {
        ArrayList<String> array_list = new ArrayList<>();
        db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor res = db.rawQuery("select * from Cancel_Request_Table ", null);
        res.moveToFirst();
        System.out.println("COUNT" + res.getCount());
        while (!res.isAfterLast()) {
            switch (get) {
                case 0:
                    array_list.add(res.getString(res.getColumnIndex(CANCEL_ID)));
                    break;
                case 1:
                    array_list.add(res.getString(res.getColumnIndex(CANCEL_REASON)));
                    break;
            }
            res.moveToNext();
        }
        return array_list;
    }


    //--------------------------OFFLINE------------------------------


    public int isDataAvailable(Context context) throws SQLException {
        DatabaseHelper helper = new DatabaseHelper(context);
        String query = "select * from PartialOnlineData";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor res = db.rawQuery(query,null);
        String password = "";
        if(res.moveToFirst())
        {
            System.out.println("@@Data available in partialOnlineData");
            db.close();
            return 0;
        }
        System.out.println("@@Data is not available in partialOnlineData");
        db.close();
        return -1;
    }

    public String[] getMonthYear(Context context) throws SQLException {
        DatabaseHelper helper = new DatabaseHelper(context);
        String query = "select * from KeyRegister";
        SQLiteDatabase db = helper.getWritableDatabase();
        ResultSet res = (ResultSet) db.rawQuery(query, null);
        String monthYear[] = new String[2];

        while (res.next()) {
            monthYear[0] = res.getString("allotedMonth");
            monthYear[1] = res.getString("allotedYear");
        }

        db.close();
        return monthYear;
    }

}




