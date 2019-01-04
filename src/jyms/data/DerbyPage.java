/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.data;

/**
 *
 * @author John
 */
public class DerbyPage {
    private String sqlQuery;
    private int total = 0; // 总记录数
    private int limit = 20; // 每页显示记录数
    private int pages = 1; // 总页数
    private int pageNumber = 1; // 当前页
     
    private boolean isFirstPage=false;        //是否为第一页
    private boolean isLastPage=false;         //是否为最后一页
    private boolean hasPreviousPage=false;   //是否有前一页
    private boolean hasNextPage=false;       //是否有下一页
    
    private int navigatePages=8; //导航页码数
    private int[] navigatePageNumbers;  //所有导航页号 已下拉列表框表示所有的导航页
    
    private String sFileName = "--->>DerbyPage.java";
    /**
     * 构造函数中，将每页显示记录数初始化
    */
//    public DerbyPage(int total, int pageNumber) {
//        init(total, pageNumber, limit);
//    }
//     
//    public DerbyPage(int total, int pageNumber, int limit) {
//        init(total, pageNumber, limit);
//    }
    
    /**
     * 构造函数中，将每页显示记录数初始化
     * @param SqlCount
     * @param SqlQuery
     * @param limit
     * @param FileName
     */
    public DerbyPage(String SqlCount, String SqlQuery, int limit, String FileName) {
        sFileName = FileName + sFileName;
        this.total = InitDB.getInitDB(sFileName).getNums(SqlCount);
        this.sqlQuery = SqlQuery;
        this.limit = limit;
    }
    
    public String getSqlQueryOfPageNumber(int PageNumber){
        //OFFSET ? ROWS FETCH NEXT ? ROWS ONLY; 
        init(PageNumber);
        return getSqlQuery() + " OFFSET " + (limit * (pageNumber - 1)) + " ROWS FETCH NEXT "+ limit + " ROWS ONLY";
    }
    
    private void init(int PageNumber){
        
        this.pages=(this.total-1)/this.limit+1;
         
        //根据输入可能错误的当前号码进行自动纠正
        if(PageNumber<1){
            this.pageNumber=1;
        }else if(PageNumber>this.pages){
            this.pageNumber=this.pages;
        }else{
            this.pageNumber=PageNumber;
        }
         
        //基本参数设定之后进行导航页面的计算
        calcNavigatePageNumbers();
         
        //以及页面边界的判定
        judgePageBoudary();
    }
    
    private void init(int total, int PageNumber, int limit){
        //设置基本参数
        this.total=total;
        this.setLimit(limit);
        init(PageNumber);
    }
    
    /**
     * 计算导航页
     */
    private void calcNavigatePageNumbers(){
        //当总页数小于或等于导航页码数时
        if(pages<=navigatePages){
            navigatePageNumbers=new int[pages];
            for(int i=0;i<pages;i++){
                navigatePageNumbers[i]=i+1;
            }
        }else{ //当总页数大于导航页码数时
            navigatePageNumbers=new int[navigatePages];
            int startNum=pageNumber-navigatePages/2;
            int endNum=pageNumber+navigatePages/2;
             
            if(startNum<1){
                startNum=1;
                //(最前navPageCount页
                for(int i=0;i<navigatePages;i++){
                    navigatePageNumbers[i]=startNum++;
                }
            }else if(endNum>pages){
                endNum=pages;
                //最后navPageCount页
                for(int i=navigatePages-1;i>=0;i--){
                    navigatePageNumbers[i]=endNum--;
                }
            }else{
                //所有中间页
                for(int i=0;i<navigatePages;i++){
                    navigatePageNumbers[i]=startNum++;
                }
            }
        }
    }
 
    /**
     * 判定页面边界
     */
    private void judgePageBoudary(){
        isFirstPage = pageNumber == 1;
        isLastPage = pageNumber == pages;
        //isLastPage = pageNumber == pages && pageNumber!=1;
        hasPreviousPage = pageNumber!=1;
        hasNextPage = pageNumber!=pages;
    }
     
    /**
     * 得到记录总数
     * @return {int}
     */
    public int getTotal() {
        return total;
    }
 
    /**
     * 得到每页显示多少条记录
     * @return {int}
     */
    public int getLimit() {
        return limit;
    }
 
    /**
     * 得到页面总数
     * @return {int}
     */
    public int getPages() {
        return pages;
    }
 
    /**
     * 得到当前页号
     * @return {int}
     */
    public int getPageNumber() {
        return pageNumber;
    }
    public boolean isFirstPage() {
        return isFirstPage;
    }
 
    public boolean isLastPage() {
        return isLastPage;
    }
 
    public boolean hasPreviousPage() {
        return hasPreviousPage;
    }
 
    public boolean hasNextPage() {
        return hasNextPage;
    }
    
    /**
     * 得到所有导航页号 
     * @return {int[]}
     */
    public int[] getNavigatePageNumbers() {
        return navigatePageNumbers;
    }
    
    public String toString(){
        String str=new String();
        str= "[" +
            "total="+total+
            ",pages="+pages+
            ",pageNumber="+pageNumber+
            ",limit="+limit+
            //",navigatePages="+navigatePages+
            ",isFirstPage="+isFirstPage+
            ",isLastPage="+isLastPage+
            ",hasPreviousPage="+hasPreviousPage+
            ",hasNextPage="+hasNextPage+
            ",navigatePageNumbers=";
        int len=navigatePageNumbers.length;
        if(len>0)str+=(navigatePageNumbers[0]);
        for(int i=1;i<len;i++){
            str+=(" "+navigatePageNumbers[i]);
        }
        //sb+=",list="+list;
        str+="]";
        return str;
    }

//    /**
//     * 在原先的搜索条件上，再附加新的搜索条件
//     */
//    public void attachSqlQuery(String aSqlQuery) {
//        this.sqlQuery = this.sqlQuery + aSqlQuery;
//    }

    /**
     * @return the sqlQuery
     */
    public String getSqlQuery() {
        return sqlQuery;
    }

    /**
     * @param limit the limit to set
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }
}
