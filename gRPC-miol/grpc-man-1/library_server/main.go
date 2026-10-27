package main

import (
	"context"
	"fmt"
	"log"
	"net"

	"spam/grpc/database"
	"spam/grpc/pb"

	"google.golang.org/grpc"
)

func main() {

	addr := ":9393"

	listen, err := net.Listen("tcp", addr)

	if err != nil {
		log.Fatalf("error: can't listen - %s", err)
	}

	defer listen.Close()
	fmt.Printf("Server running on port: %s", addr)

	db, err := database.Open("database/library.db")

	if err != nil {
		log.Fatal(err)
	}

	defer db.Close()

	server := grpc.NewServer()

	libraryServer := &LibraryServer{
		db:       db,
		newBooks: make(chan *pb.Book, 10),
	}

	pb.RegisterLibraryServer(server, libraryServer)

	server.Serve(listen)

}

type LibraryServer struct {
	pb.UnimplementedLibraryServer
	db       *database.DB
	newBooks chan *pb.Book
}

func (server *LibraryServer) GetBookById(
	ctx context.Context,
	request *pb.GetBookByIdRequest,
) (*pb.Book, error) {

	log.Printf("Received GetBookById request: %d", request)

	book, err := server.db.GetBookById(ctx, request.GetBookId())

	log.Printf("Book received from database: %o", book)

	if err != nil {
		return nil, err
	}

	return &pb.Book{
		BookId:              book.ID,
		Title:               book.Title,
		AuthorId:            book.AuthorID,
		PublishingYear:      book.PublishingYear,
		PublishingCompanyId: book.PublishingCompanyID,
	}, nil

}

func (server *LibraryServer) CreateBook(
	ctx context.Context,
	request *pb.CreateBookRequest,
) (*pb.CreateBookResponse, error) {

	book_id, err := server.db.CreateBook(
		ctx,
		request.GetTitle(),
		request.GetAuthorId(),
		request.GetPublishingCompanyId(),
		request.GetPublishingYear(),
	)

	if err != nil {
		return nil, err
	}

	book := &pb.Book{
		BookId:              int32(book_id),
		Title:               request.GetTitle(),
		AuthorId:            request.GetAuthorId(),
		PublishingYear:      request.GetPublishingYear(),
		PublishingCompanyId: request.GetPublishingCompanyId(),
	}

	select {
	case server.newBooks <- book:
	default:
	}

	return &pb.CreateBookResponse{Book: book}, nil

}

func (server *LibraryServer) WatchBooks(request *pb.WatchBooksRequest, stream grpc.ServerStreamingServer[pb.Book]) error {

	for {
		select {
		case book := <-server.newBooks:
			if err := stream.Send(book); err != nil {
				return err
			}
		case <-stream.Context().Done():
			return nil
		}
	}
}
