package com.example.proyectofinal.service;

import com.example.proyectofinal.dto.*;
import com.example.proyectofinal.entity.Book;
import com.example.proyectofinal.exceptions.NotFoundException;
import com.example.proyectofinal.repository.BookRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookService implements IBookService {

    BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    ModelMapper mapper = new ModelMapper();


    //Retorna una lista con todos los libros
    @Override
    public List<BookDto> findAllBooks() {

        List<Book> bookList = bookRepository.findAll();
        List<BookDto> bookDtoList = new ArrayList<>();
        bookList.stream().forEach(b -> bookDtoList.add(mapper.map(b, BookDto.class)));

        return bookDtoList;
    }

    //Busca un libro por su Id
    @Override
    public BookDto findBookById(Long id) {

        Optional<Book> book = bookRepository.findById(id);
        if(book.isPresent()) {
            Book b = book.get();
            BookDto response = mapper.map(b, BookDto.class);

            return response;
        }

        throw new NotFoundException("El libro solicitado no fue encontrado");
    }

    //Retorna una lista con todos los libros ordenados alfabéticamente por el título
    @Override
    public List<BookDto> findBookByOrderByTitleAsc() {

        List<Book> bookList = bookRepository.findBookByOrderByTitleAsc();
        List<BookDto> bookDtoList = new ArrayList<>();
        bookList.stream().forEach(b -> bookDtoList.add(mapper.map(b, BookDto.class)));

        return bookDtoList;
    }

    //Busca libros por su autor
    @Override
    public List<BookDto> findBookByAuthor(String author) {

        List<Book> bookList = bookRepository.findBookByAuthorContainingIgnoreCase(author);
        if(!bookList.isEmpty()) {
            List<BookDto> bookDtoList = new ArrayList<>();
            bookList.stream().forEach(b -> bookDtoList.add(mapper.map(b, BookDto.class)));

            return bookDtoList;
        }

        throw new NotFoundException("No hay ningún libro que coincida con la búsqueda");
    }

    //Actualiza los datos de un libro, indicado por su Id
    @Override
    public RespBookDto updateBookById(Long id, BookDto bookDto) {

        Optional<Book> book = bookRepository.findById(id);
        if(book.isPresent()) {
            Book bExist = book.get();
            Book bookUpdated = (mapper.map(bookDto, Book.class));

            bookUpdated.setId(bExist.getId());
            bookUpdated.setEditorial(bExist.getEditorial());

            bookRepository.save(bookUpdated);

            RespBookDto response = new RespBookDto();
            response.setBook(mapper.map(bookUpdated, BookDto.class));
            response.setResponse("El libro se actualizó exitosamente");

            return response;
        }

        throw new NotFoundException("El libro solicitado no fue encontrado");
    }

    //Elimina un libro por su Id
    @Override
    public RespMessageDto deleteBookById(Long id) {

        Optional<Book> book = bookRepository.findById(id);
        if(book.isPresent()) {
            bookRepository.deleteById(id);

            RespMessageDto response = new RespMessageDto();
            response.setResponse("El libro se eliminó exitosamente");

            return response;
        }

        throw new NotFoundException("No se puede eliminar. El libro seleccionado no existe");
    }
}
