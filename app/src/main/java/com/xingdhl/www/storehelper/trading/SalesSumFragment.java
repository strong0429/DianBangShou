package com.xingdhl.www.storehelper.trading;

import android.support.v4.app.Fragment;

import com.xingdhl.www.storehelper.ObjectDefine.Categories;
import com.xingdhl.www.storehelper.ObjectDefine.SalesRecord;

import java.util.ArrayList;
import java.util.List;

public abstract class SalesSumFragment extends Fragment {
    public abstract void setObjectList(List<?> objectList);
    public abstract void updateText(double... data);
    public abstract List<Float> getSums(List<?> records);

    protected static class SalesSumRecord{
        String mItemName;
        String mBarcode;

        float mSalesTotal;
        float mProfitTotal;
        //float mProfitRate;
        //float mProfitProportion;

        short mCategory;

        private SalesSumRecord(String itemName, String barcode, float salesTotal,
                               float profitTotal, short category){
            mItemName = itemName;
            mBarcode = barcode;
            mSalesTotal = salesTotal;
            mProfitTotal = profitTotal;
            mCategory = category;
        }

        protected float getProfitRate(){
            if(mSalesTotal > 0)
                return mProfitTotal / mSalesTotal * 100;
            else
                return 0;
        }
    }

    public static List<SalesSumRecord> getSumsByGoods(List<SalesRecord> salesRecords){
        List<SalesSumRecord> salesSumRecords = new ArrayList<>();

        boolean isFinded;
        for(SalesRecord sr : salesRecords){
            isFinded = false;
            for(SalesSumRecord ssr : salesSumRecords){
                if(sr.getBarcode().equals(ssr.mBarcode)){
                    ssr.mSalesTotal += sr.getSaleSum();
                    ssr.mProfitTotal += sr.getProfit();
                    isFinded = true;
                    break;
                }
            }
            if(isFinded) continue;
            salesSumRecords.add(new SalesSumRecord(sr.getGoodsName(),sr.getBarcode(),
                    sr.getSaleSum(), sr.getProfit(), sr.getGoodsCategory()));
        }

        return salesSumRecords;
    }

    public static List<SalesSumRecord> getSumsByCategory(List<SalesRecord> salesRecords){
        List<SalesSumRecord> salesSumRecords = new ArrayList<>();

        boolean isFinded;
        for(SalesRecord sr : salesRecords){
            isFinded = false;
            for(SalesSumRecord ssr : salesSumRecords){
                if(sr.getGoodsCategory() == ssr.mCategory){
                    ssr.mSalesTotal += sr.getSaleSum();
                    ssr.mProfitTotal += sr.getProfit();
                    isFinded = true;
                    break;
                }
            }
            if(isFinded) continue;

            SalesSumRecord newItem = new SalesSumRecord(sr.getGoodsName(),sr.getBarcode(),
                    sr.getSaleSum(), sr.getProfit(), sr.getGoodsCategory());
            for(Categories.Category gCategory : Categories.getGoodsCategories()){
                if(gCategory.getId() != sr.getGoodsCategory()) continue;
                newItem.mItemName = gCategory.getName();
                break;
            }
            salesSumRecords.add(newItem);
        }

        return salesSumRecords;
    }
}
