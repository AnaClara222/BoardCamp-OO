package com.boardcamp.api.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.RentalDTO;
import com.boardcamp.api.exceptions.BadRequestException;
import com.boardcamp.api.exceptions.NotFoundException;
import com.boardcamp.api.exceptions.UnprocessableEntityException;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.models.RentalModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.repositories.RentalRepository;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final CustomerRepository customerRepository;
    private final GameRepository gameRepository;

    public RentalService(RentalRepository rentalRepository,
                         CustomerRepository customerRepository,
                         GameRepository gameRepository) {
        this.rentalRepository = rentalRepository;
        this.customerRepository = customerRepository;
        this.gameRepository = gameRepository;
    }

    public RentalModel createRental(RentalDTO dto) {

        if (dto.getDaysRented() == null || dto.getDaysRented() <= 0) {
            throw new BadRequestException("DaysRented must be greater than 0.");
        }

        CustomerModel customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found."));

        GameModel game = gameRepository.findById(dto.getGameId())
                .orElseThrow(() -> new NotFoundException("Game not found."));
                
        Long rentedCount = (long) rentalRepository.countByGameIdAndReturnDateIsNull(game.getId());
        if (rentedCount >= game.getStockTotal()) {
            throw new UnprocessableEntityException("Game is out of stock.");
        }

        RentalModel rental = new RentalModel();
        rental.setCustomer(customer);
        rental.setGame(game);
        rental.setDaysRented(dto.getDaysRented());

        rental.setRentDate(LocalDate.now());
        rental.setReturnDate(null);
        rental.setDelayFee(0L);

        Long originalPrice = (long) dto.getDaysRented() * game.getPricePerDay();
        rental.setOriginalPrice(originalPrice);

        return rentalRepository.save(rental);
    }

    public List<RentalModel> getAllRentals() {
        return rentalRepository.findAll().stream().toList();
    }

    public RentalModel returnRental(Long rentalId) {
        
        RentalModel rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new NotFoundException("Rental not found."));

        if (rental.getReturnDate() != null) {
            throw new UnprocessableEntityException("This rental is already finished.");
        }

        LocalDate today = LocalDate.now();
        rental.setReturnDate(today);

        LocalDate expectedReturnDate = rental.getRentDate().plusDays(rental.getDaysRented());
        long delayDays = Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(expectedReturnDate, today));

        Long delayFee = delayDays * rental.getGame().getPricePerDay();
        rental.setDelayFee(delayFee);

        return rentalRepository.save(rental);
    }

    public void deleteRental(Long id) {
        RentalModel rental = rentalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rental not found."));

        if (rental.getReturnDate() == null) {
            throw new UnprocessableEntityException("Cannot delete an active rental.");
        }

        rentalRepository.delete(rental);
    }
}
