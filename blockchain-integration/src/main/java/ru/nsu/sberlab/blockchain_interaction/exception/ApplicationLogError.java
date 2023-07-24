package ru.nsu.sberlab.blockchain_interaction.exception;

import io.neow3j.protocol.core.Response;


public class ApplicationLogError extends BlockChainException {


    Response.Error e;

    public ApplicationLogError(Response.Error e) {
        super("Error fetching transaction's app log: " + e.getMessage());
        this.e = e;
    }

    public Response.Error getError() {
        return e;
    }
}
