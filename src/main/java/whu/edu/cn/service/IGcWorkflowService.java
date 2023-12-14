package whu.edu.cn.service;

import whu.edu.cn.entity.coverage.CoverageSubset;
import whu.edu.cn.entity.modify.ModifyParam;

public interface IGcWorkflowService {
    CoverageSubset modifyProperties(CoverageSubset coverageSubset, ModifyParam modifyParam);
}
