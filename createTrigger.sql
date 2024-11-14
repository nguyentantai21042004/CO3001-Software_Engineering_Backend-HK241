USE CNPM;

--
-- Trigger to calculate print_job_total_page
--

DELIMITER $$

CREATE TRIGGER calculate_total_pages
BEFORE INSERT ON print_jobs
FOR EACH ROW
BEGIN
    DECLARE total_pages INT;

    -- Calculate the number of pages based on the number of commas in the page_number string
    SET total_pages = LENGTH(NEW.page_number) - LENGTH(REPLACE(NEW.page_number, ',', '')) + 1;

    -- Check if printing is double-sided
    IF NEW.page_side = 'double sided' THEN
        -- For double-sided printing, divide the total pages by 2 (rounding up for odd numbers)
        SET total_pages = CEIL(total_pages / 2);
    END IF;

    -- Multiply by the number of copies
    SET NEW.total_page = total_pages * NEW.number_of_copies;
END $$

DELIMITER ;

--
-- Trigger to calculate print_job_total_cost
--

DELIMITER $$

CREATE TRIGGER calculate_total_cost
BEFORE INSERT ON print_jobs
FOR EACH ROW
BEGIN
    -- Declare a variable to hold the cost per page
    DECLARE cost_per_page INT;

    -- Retrieve the paper size and assign the corresponding cost
    SET cost_per_page = (
        CASE
            WHEN NEW.page_size = 'A4' THEN 1
            WHEN NEW.page_size = 'A3' THEN 2
            ELSE 0  -- Default cost if page size is not recognized
        END
    );

    -- Calculate the total cost by multiplying the total pages by the cost per page
    SET NEW.total_cost = NEW.total_page * cost_per_page;
END $$

DELIMITER ;

--
-- Trigger to calculate student_balance after payment
--

DELIMITER $$

CREATE TRIGGER update_student_balance_after_payment
AFTER INSERT ON payments
FOR EACH ROW
BEGIN
    -- Update the balance in the students table
    IF NEW.status = 'successful' THEN
		UPDATE students
		SET student_balance = student_balance + NEW.balance
		WHERE id = NEW.student_id;
	END IF;
END $$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER update_student_balance_after_payment_update
AFTER UPDATE ON payments
FOR EACH ROW
BEGIN
    -- Check if the status was updated to 'successful'
    IF NEW.status = 'successful' AND OLD.status != 'successful' THEN
        -- Add the payment balance to the student's balance
        UPDATE students
        SET student_balance = student_balance + NEW.balance
        WHERE id = NEW.student_id;

    -- If the status changed from 'successful' to something else, revert the balance
    ELSEIF OLD.status = 'successful' AND NEW.status != 'successful' THEN
        -- Subtract the payment balance from the student's balance
        UPDATE students
        SET student_balance = student_balance - OLD.balance
        WHERE id = NEW.student_id;
    END IF;
END $$

DELIMITER ;

--
-- Trigger to insert student_balance after create
--

DELIMITER $$

CREATE TRIGGER update_student_balance_before_insert
BEFORE INSERT ON students
FOR EACH ROW
BEGIN
    DECLARE latest_allocation INT;

    -- Get the latest allocation where status is 'allocated'
    SELECT number_of_pages
    INTO latest_allocation
    FROM page_allocations
    WHERE status = 'allocated'
    ORDER BY date DESC
    LIMIT 1;

    -- Set the student balance to the latest allocation of pages
    IF latest_allocation IS NOT NULL THEN
        SET NEW.student_balance = latest_allocation;
    ELSE
        -- Default balance if no allocation found
        SET NEW.student_balance = 0;
    END IF;
END $$

DELIMITER ;

--
-- Trigger to update student_balance when page_allocation_status is updated to 'allocated'
--

DELIMITER $$

CREATE TRIGGER increase_student_balance_after_allocation
AFTER UPDATE ON page_allocations
FOR EACH ROW
BEGIN
    -- Check if the new status is 'allocated' and the old status was not
    IF NEW.status = 'allocated' AND OLD.status != 'allocated' THEN
        -- Increase the student's balance by the number of allocated pages
        UPDATE students
        SET student_balance = student_balance + NEW.number_of_pages;
    END IF;
END $$

DELIMITER ;

--
-- Trigger to update student_balance when page_allocation_status is 'allocated'
--

DELIMITER $$

CREATE TRIGGER increase_student_balance_after_insert
AFTER INSERT ON page_allocations
FOR EACH ROW
BEGIN
    -- Check if the inserted status is 'allocated'
    IF NEW.status = 'allocated' THEN
        -- Increase the student's balance by the number of allocated pages
        UPDATE students
        SET student_balance = student_balance + NEW.number_of_pages;
    END IF;
END $$

DELIMITER ;

--
-- Trigger to check if the date column
-- If the date is less than the current date (CURDATE()), an error is raised (as before).
-- If the date is equal to the current date, the status is automatically set to 'allocated'.
-- If the date is greater than the current date (a future date), the status is set to 'pending'.
--

DELIMITER $$

CREATE TRIGGER check_page_allocation_date
BEFORE INSERT ON page_allocations
FOR EACH ROW
BEGIN
    -- If the date is in the past, raise an error
    IF NEW.date < CURDATE() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cannot insert a page allocation with a date in the past';

    -- If the date is today, automatically set status to 'allocated'
    ELSEIF NEW.date = CURDATE() THEN
        SET NEW.status = 'allocated';

    -- If the date is in the future, set status to 'pending'
    ELSEIF NEW.date > CURDATE() THEN
        SET NEW.status = 'pending';
    END IF;
END $$

DELIMITER ;

--
-- Trigger to calculate student_balance after print
--

DELIMITER $$

CREATE TRIGGER update_student_balance_after_print
AFTER INSERT ON print_jobs
FOR EACH ROW
BEGIN
    -- Check if the print job status is 'successful'
    IF NEW.status = 'successful' THEN
        -- Deduct the total cost of the print job from the student's balance
        UPDATE students
        SET student_balance = student_balance - NEW.total_cost
        WHERE id = NEW.student_id;
    END IF;
END $$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER update_student_balance_after_printjob_update
AFTER UPDATE ON print_jobs
FOR EACH ROW
BEGIN
    -- Check if the status was updated to 'successful'
    IF NEW.status = 'successful' AND OLD.status != 'successful' THEN
        -- Deduct the print job total cost from the student's balance
        UPDATE students
        SET student_balance = student_balance - NEW.total_cost
        WHERE id = NEW.student_id;

    -- If the status changed from 'successful' to something else, refund the balance
    ELSEIF OLD.status = 'successful' AND NEW.status != 'successful' THEN
        -- Add the total cost back to the student's balance
        UPDATE students
        SET student_balance = student_balance + OLD.total_cost
        WHERE id = NEW.student_id;
    END IF;
END $$

DELIMITER ;

--
-- Auto check allocate pages by day
--

DELIMITER $$
CREATE EVENT IF NOT EXISTS allocate_pages_daily
ON SCHEDULE EVERY 1 DAY
STARTS (TIMESTAMP(CURRENT_DATE) + INTERVAL 1 DAY) -- check at '00:00:00'
DO
BEGIN
    -- Update page_allocations
    UPDATE page_allocations
    SET status = 'allocated'
    WHERE status = 'pending'
      AND date = CURDATE();

END$$

DELIMITER ;