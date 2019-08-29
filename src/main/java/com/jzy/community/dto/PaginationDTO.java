package com.jzy.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jzy
 * @create 2019-08-11-9:27
 */
@Data
public class PaginationDTO<T> {
    private List<T> data;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEnd;
    private Integer page;
    //前端需要显示的页数列表
    private List<Integer> pages = new ArrayList<>();
    private Integer totalPage;

    public void setPagination(Integer totalcount, Integer page, Integer size) {

        //根据数据库中的问题条数计算出总页数
        if (totalcount == 0){
            totalPage =1;
        }
        else if(totalcount %size==0){
            totalPage = totalcount/size;
        }else {
            totalPage = totalcount/size+1;
        }
        if (page <1){
            page=1;
        }
        if (page>totalPage){
            page=totalPage;
        }
        this.page=page;
        //当前页放进pages

        pages.add(page);
        //根据当前页计算pages的值
        for(int i =1;i<=3;i++){
            if (page-i>0){
                pages.add(0,page-i);
            }
            if (page+i<=totalPage){
                pages.add(page+i);
            }
        }



        //是否展示上一页
        if(page == 1){
            showPrevious=false;
        }else {
            showPrevious=true;
        }
        //是否展示下一页
        if(page== totalPage){
            showNext=false;
        }else {
            showNext=true;
        }
        //是否展示第一页
        if (pages.contains(1)){
            showFirstPage=false;
        }else {
            showFirstPage=true;
        }
        //是否展示最后一页
        if (pages.contains(totalPage)){
            showEnd = false;
        }else {
            showEnd = true;
        }
    }
}
