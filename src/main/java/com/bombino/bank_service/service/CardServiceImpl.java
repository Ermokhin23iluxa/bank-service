package com.bombino.bank_service.service;

import com.bombino.bank_service.configuration.CryptoUtils;
import com.bombino.bank_service.exception.CardNotFoundException;
import com.bombino.bank_service.model.dto.CardDto;
import com.bombino.bank_service.model.entity.Card;
import com.bombino.bank_service.model.enums.CardStatus;
import com.bombino.bank_service.model.mapper.CardMapper;
import com.bombino.bank_service.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final GenerationCardService generationCardService;
    private final CardRepository cardRepository;
    private final CardMapper mapper;
    private final BigDecimal startMoney = new BigDecimal(0);
    private final EncryptionService encryptionService;

    @Override
    public CardDto createCard(UUID userId) {
        log.debug("Создание карты");
        String pan = generationCardService.generateUniquePan();
        log.info("pan:{} ,длина:{} ",pan,pan.length());
        String panHash = CryptoUtils.sha256Hex(pan);
        log.info("panHash:{} ,длина:{} ",panHash,panHash.length());
        String cryptedPan = encryptionService.encrypt(pan);
        log.info("cryptedPan:{} ,длина:{} ",cryptedPan,cryptedPan.length());
        String maskedPan = encryptionService.maskPan(pan);
        log.info("maskedPan:{} ,длина:{} ",maskedPan,maskedPan.length());
        LocalDate expirationDate = generationCardService.generationDate(LocalDate.now());

        String cvv = generationCardService.generationCVV();
        log.info("cvv:{} ,длина:{} ",cvv,cvv.length());
        String encryptedCVV = encryptionService.encrypt(cvv);
        log.info("encryptedCVV:{} ,длина:{} ",encryptedCVV,encryptedCVV.length());

        Card newCard = Card
                .builder()
                .userId(userId)
                .panHash(panHash)
                .panCiphertext(cryptedPan)
                .maskedPan(maskedPan)
                .cvvCiphertext(encryptedCVV)
                .expireDate(expirationDate)
                .balance(startMoney)
                .currency("RUB")
                .status(CardStatus.ACTIVE)
                .build();

        cardRepository.save(newCard);

        log.info("Успешное создание карты id: {}", newCard.getId());

        return mapper.toDto(newCard);
    }


    /**
     *  Для админа - достать все карты пользователя
     */
    @Override
    public List<CardDto> getCardsByUserId(UUID userId){
        List<Card> cardsByUserId = cardRepository.findByUserId(userId);
        if(cardsByUserId.isEmpty()){
            throw new CardNotFoundException("У пользователя пока нет карт!");
        }
        return mapper.toDto(cardsByUserId);

    }

    @Override
    public void deleteCard(UUID id) {
        log.debug("Удаление карты с ID: {}", id);
        Card card = findCardByIdOrThrow(id);
        cardRepository.delete(card);
        log.info("Карта с ID {} успешно удалена", id);
    }

    @Override
    public CardDto getCardById(UUID id) {
        log.debug("Извлечение карты с ID: {}", id);
        Card card = findCardByIdOrThrow(id);
        log.info("Карта с ID {} успешно извлечена", id);
        return mapper.toDto(card);
    }

    /**
     *  Для админа - достать все карты в системе
     */
    @Override
    public List<CardDto> getAllCards() {
        log.debug("Извлечение всех карт");
        List<Card> cards = cardRepository.findAll();
        log.info("Карта успешно извлечены");
        return cards.stream().map(mapper::toDto).toList();
    }

    //todo: Доработать логику, что МЕНЯТЬ СТАТУС может только ADMIN
    public CardDto changeStatus(UUID id, CardStatus status) {
        Card card = findCardByIdOrThrow(id);
        if (card.getStatus() == status) {
            throw new IllegalStateException("Этот статус уже установлен: {" + status.name() + "}");
        }
        card.setStatus(status);
        cardRepository.save(card);
        log.info("Статус успешно изменен на: {}", status.name());
        return mapper.toDto(card);
    }

    //todo: Продление карты, доработать
    public CardDto cardRenewal(UUID id) {
        log.debug("Попытка продления карты {}", id);
        Card card = findCardByIdOrThrow(id);
        LocalDate newDate = generationCardService.generationDate(card.getExpireDate());
        card.setExpireDate(newDate);
        cardRepository.save(card);
        log.info("Успешное продление карты до {}", card.getExpireDate());
        return mapper.toDto(card);
    }

    private Card findCardByIdOrThrow(UUID id) {
        log.debug("Поиск карты с id: {}", id);
        return cardRepository.findById(id)
                .orElseThrow(
                        () -> {
                            log.warn("Карта с ID {} не найдена", id);
                            return new CardNotFoundException("Карты с id: {" + id + "} не существует");
                        });
    }


}
