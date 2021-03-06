/*
 * Copyright (C) 2015 Arthur Gregorio, AG.Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.webbudget.application.controller.entries;

import br.com.webbudget.application.controller.AbstractBean;
import br.com.webbudget.domain.entity.card.Card;
import br.com.webbudget.domain.entity.card.CardType;
import br.com.webbudget.domain.entity.wallet.Wallet;
import br.com.webbudget.domain.service.CardService;
import br.com.webbudget.domain.service.WalletService;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 06/04/2014
 */
@ViewScoped
@ManagedBean
public class CardBean extends AbstractBean {

    @Getter
    private Card card;
    @Getter
    private List<Card> cards;
    @Getter
    public List<Wallet> wallets;

    @Setter
    @ManagedProperty("#{cardService}")
    private CardService cardService;
    @Setter
    @ManagedProperty("#{walletService}")
    private WalletService walletService;

    /**
     *
     * @return
     */
    @Override
    protected Logger initializeLogger() {
        return LoggerFactory.getLogger(CardBean.class);
    }

    /**
     *
     */
    public void initializeListing() {
        this.viewState = ViewState.LISTING;
        this.cards = this.cardService.listCards(null);
    }

    /**
     *
     * @param cardId
     */
    public void initializeForm(long cardId) {

        this.wallets = this.walletService.listWallets(false);

        if (cardId == 0) {
            this.viewState = ViewState.ADDING;
            this.card = new Card();
        } else {
            this.viewState = ViewState.EDITING;
            this.card = this.cardService.findCardById(cardId);
        }
    }

    /**
     *
     * @return
     */
    public String changeToAdd() {
        return "formCard.xhtml?faces-redirect=true";
    }

    /**
     *
     * @return
     */
    public String changeToListing() {
        return "listCards.xhtml?faces-redirect=true";
    }

    /**
     *
     * @param cardId
     * @return
     */
    public String changeToEdit(long cardId) {
        return "formCard.xhtml?faces-redirect=true&cardId=" + cardId;
    }

    /**
     *
     * @param cardId
     */
    public void changeToDelete(long cardId) {
        this.card = this.cardService.findCardById(cardId);
        this.openDialog("deleteCardDialog", "dialogDeleteCard");
    }

    /**
     * Cancela e volta para a listagem
     *
     * @return
     */
    public String doCancel() {
        return "listCards.xhtml?faces-redirect=true";
    }

    /**
     *
     */
    public void doSave() {

        try {
            this.cardService.saveCard(this.card);
            this.card = new Card();

            this.info("card.action.saved", true);
        } catch (Exception ex) {
            this.logger.error("CardBean#doSave found erros", ex);
            this.fixedError(ex.getMessage(), true);
        }
    }

    /**
     *
     */
    public void doUpdate() {

        try {
            this.card = this.cardService.updateCard(this.card);

            this.info("card.action.updated", true);
        } catch (Exception ex) {
            this.logger.error("CardBean#doUpdate found erros", ex);
            this.fixedError(ex.getMessage(), true);
        }
    }

    /**
     *
     */
    public void doDelete() {

        try {
            this.cardService.deleteCard(this.card);
            this.cards = this.cardService.listCards(false);

            this.info("card.action.deleted", true);
        } catch (Exception ex) {
            this.logger.error("CardBean#doDelete found erros", ex);
            this.fixedError(ex.getMessage(), true);
        } finally {
            this.update("cardsList");
            this.closeDialog("dialogDeleteCard");
        }
    }

    /**
     *
     * @return
     */
    public CardType[] getAvailableCardTypes() {
        return CardType.values();
    }
}
