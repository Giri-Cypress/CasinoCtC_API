package com.CasinoCtC.CCtCAPI.service;

import com.CasinoCtC.CCtCAPI.dto.TransactionRequest;
import com.CasinoCtC.CCtCAPI.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.CasinoCtC.CCtCAPI.dao.UserDAO;
import com.CasinoCtC.CCtCAPI.dao.TransactionDAO;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl
        implements TransactionService {

    private final TransactionDAO transactionDAO;

    private final UserDAO userDAO;


	@Override
	public Object getTransactionById(Long id) {
	
	    return transactionDAO.getTransactionById(id);
    }

    @Override
    @Transactional
    public void saveTransaction(
            TransactionRequest request,
            String username
    ) {

        User user =
                userDAO
                        .findByUsername(username)
                        .orElseThrow();

        transactionDAO.saveTransaction(

                request,

                user.getUserId(),

                user.getLocationId()

        );

    }

}