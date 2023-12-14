package whu.edu.cn.service.impl;

import org.springframework.stereotype.Service;
import whu.edu.cn.entity.coverage.CoverageSubset;
import whu.edu.cn.entity.modify.ModifyParam;
import whu.edu.cn.service.IGcWorkflowService;

@Service
public class GcWorkflowServiceImpl implements IGcWorkflowService {

    public CoverageSubset modifyProperties(CoverageSubset coverageSubset, ModifyParam modifyParam){
        String usedBands = modifyParam.getUsedBands();
        // 注意 这里相当于忽略了用户输入对结果波段的选择，如果usedBands存在 则替换为properties
        // 如果usdBands为null 也会赋给properties
        coverageSubset.setProperties(usedBands);
        return coverageSubset;
    }

}
