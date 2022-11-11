package com.ll.ebook.app.withdraw.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class WithdrawForm {
    @NotEmpty
    private String bankName;
    @NotEmpty
    private String bankAccountNo;
    @NotNull
    private int price;


}
