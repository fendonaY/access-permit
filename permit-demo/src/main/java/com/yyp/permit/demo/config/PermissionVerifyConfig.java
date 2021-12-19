package com.yyp.permit.demo.config;

import com.yyp.permit.demo.dao.ModelRepository;
import com.yyp.permit.demo.model.StockBo;
import com.yyp.permit.demo.model.UserBo;
import com.yyp.permit.demo.model.dto.BuyGoodsDto;
import com.yyp.permit.dept.verifier.FunctionalVerify;
import com.yyp.permit.dept.verifier.repository.AbstractVerifyRepository;
import com.yyp.permit.dept.verifier.repository.VerifyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class PermissionVerifyConfig {

    private VerifyRepository verifyRepository;

    private ModelRepository modelRepository;

    @PostConstruct
    public void initVerifyRepository() {
        AbstractVerifyRepository verifyRepository = getVerifyRepository();
        checkBuyGoods(verifyRepository);
        checkExistUser(verifyRepository);
    }

    public void checkBuyGoods(AbstractVerifyRepository verifyRepository) {
        verifyRepository.addPermitRepository("buyGoods", (FunctionalVerify) verifyReport -> {
            Object argument = verifyReport.getArguments()[0];
            Assert.isAssignable(BuyGoodsDto.class, argument.getClass(), "参数不匹配");
            BuyGoodsDto buyGoodsDto = (BuyGoodsDto) argument;
            try {
                UserBo user = modelRepository.getUser(buyGoodsDto.getUserId());
                StockBo stock = modelRepository.getStock(buyGoodsDto.getGoodsId());
                Map<String, Object> result = new HashMap<>(2);
                result.put("user", user);
                result.put("stock", stock);
                verifyReport.setResult(Arrays.asList(result));
                if (stock.getCount() < buyGoodsDto.getCount()) {
                    verifyReport.setSuggest("库存不够，不支持购买");
                    return 0;
                }
                double sumAmount = stock.getAmount() * buyGoodsDto.getCount();
                if (user.getAsset() < sumAmount) {
                    verifyReport.setSuggest("钱不够，别买了！");
                    return 0;
                }
            } catch (Exception e) {
                return 0;
            }
            return 1;
        });
    }

    public void checkExistUser(AbstractVerifyRepository verifyRepository) {
        verifyRepository.addPermitRepository("existUser", (FunctionalVerify) verifyReport -> {
            Object[] validData = verifyReport.getValidData();
            Assert.notEmpty(validData, "参数不匹配");
            Integer userId = (Integer) validData[0];
            try {
                modelRepository.getUser(userId);
            } catch (Exception e) {
                verifyReport.setSuggest(userId + " 用户不存在");
                return 0;
            }
            return 1;
        });
    }


    public AbstractVerifyRepository getVerifyRepository() {
        return (AbstractVerifyRepository) verifyRepository;
    }

    @Autowired
    public void setVerifyRepository(VerifyRepository verifyRepository) {
        this.verifyRepository = verifyRepository;
    }

    public ModelRepository getModelRepository() {
        return modelRepository;
    }

    @Autowired
    public void setModelRepository(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }
}
