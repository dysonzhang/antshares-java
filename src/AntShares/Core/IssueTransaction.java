﻿package AntShares.Core;

import java.io.IOException;
import java.util.*;

import AntShares.*;
import AntShares.IO.*;
import AntShares.IO.Json.*;

public class IssueTransaction extends Transaction
{
	public int nonce; // unsigned int
	
	public IssueTransaction()
	{
		super(TransactionType.IssueTransaction);
	}
	
	@Override
	protected void deserializeExclusiveData(BinaryReader reader) throws IOException
	{
		nonce = reader.readInt();
	}
	
	@Override
	public UInt160[] getScriptHashesForVerifying()
	{
        HashSet<UInt160> hashes = new HashSet<UInt160>(Arrays.asList(super.getScriptHashesForVerifying()));
        for (TransactionResult result : Arrays.stream(getTransactionResults()).filter(p -> p.amount.compareTo(Fixed8.ZERO) < 0).toArray(TransactionResult[]::new))
        {
            Transaction tx;
			try
			{
				tx = Blockchain.current().getTransaction(result.assetId);
			}
			catch (Exception ex)
			{
				throw new IllegalStateException(ex);
			}
            if (tx == null || !(tx instanceof RegisterTransaction)) throw new IllegalStateException();
            RegisterTransaction asset = (RegisterTransaction)tx;
            hashes.add(asset.admin);
        }
        return hashes.stream().sorted().toArray(UInt160[]::new);
	}
	
	@Override
    public JObject json()
    {
        JObject json = super.json();
        json.set("nonce", new JNumber(Integer.toUnsignedLong(nonce)));
        return json;
    }
	
	@Override
	protected void serializeExclusiveData(BinaryWriter writer) throws IOException
	{
		writer.writeInt(nonce);
	}
	
	@Override
	public Fixed8 systemFee()
	{
		//TODO: mainnet
		return Fixed8.ZERO;
	}
	
	@Override
	public boolean verify()
	{
		//TODO
		return super.verify();
	}
}
