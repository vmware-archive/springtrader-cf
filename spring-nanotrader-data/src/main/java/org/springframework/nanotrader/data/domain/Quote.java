package org.springframework.nanotrader.data.domain;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * quote DTO object
 * 
 * @author jgordon
 *
 */
public class Quote implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer quoteid;

	private BigDecimal low;

	private BigDecimal open1;

	private BigDecimal volume;

	private BigDecimal price;

	private BigDecimal high;

	private String companyname;

	private String symbol;

	private BigDecimal change1;

	private int version;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Integer getQuoteid() {
		return quoteid;
	}

	public void setQuoteid(Integer quoteid) {
		this.quoteid = quoteid;
	}

	public BigDecimal getLow() {
		return low;
	}

	public void setLow(BigDecimal low) {
		this.low = low;
	}

	public BigDecimal getOpen1() {
		return open1;
	}

	public void setOpen1(BigDecimal open1) {
		this.open1 = open1;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getHigh() {
		return high;
	}

	public void setHigh(BigDecimal high) {
		this.high = high;
	}

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public BigDecimal getChange1() {
		return change1;
	}

	public void setChange1(BigDecimal change1) {
		this.change1 = change1;
	}

	@Override
	public int hashCode() {
		if (getQuoteid() == null) {
			return 0;
		}
		return getQuoteid().intValue();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof Quote)) {
			return false;
		}

		if (((Quote) o).getQuoteid() == this.getQuoteid()) {
			return true;
		}
		return false;
	}
}