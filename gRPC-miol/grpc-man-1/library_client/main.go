package main

import (
	"bufio"
	"context"
	"fmt"
	"io"
	"log"
	"os"
	"strconv"
	"strings"
	"time"

	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"

	"spam/grpc/pb"
)

func main() {

	addr := "localhost:9393"
	creds := insecure.NewCredentials()

	conn, err := grpc.NewClient(
		addr,
		grpc.WithTransportCredentials(creds),
	)

	if err != nil {
		log.Fatal(err)
	}

	defer conn.Close()

	client := pb.NewLibraryClient(conn)

	scanner := bufio.NewScanner(os.Stdin)

	for {
		fmt.Println("\n--- Library Client ---")
		fmt.Println("1. Get book by ID")
		fmt.Println("2. Watch books")
		fmt.Println("0. Exit")
		fmt.Print("Choose an option: ")

		if !scanner.Scan() {
			break
		}

		switch strings.TrimSpace(scanner.Text()) {
		case "1":
			getBookById(client, scanner)

		case "2":
			watchBooks(client)

		case "0":
			return
		}
	}
}

func getBookById(client pb.LibraryClient, scanner *bufio.Scanner) {
	fmt.Print("Enter book ID: ")

	if !scanner.Scan() {
		return
	}

	ctx, cancel := context.WithTimeout(
		context.Background(),
		3600*time.Second,
	)
	defer cancel()

	id, err := strconv.ParseInt(strings.TrimSpace(scanner.Text()), 10, 32)

	if err != nil {
		fmt.Println("Invalid id")
		return
	}

	book, err := client.GetBookById(
		ctx,
		&pb.GetBookByIdRequest{
			BookId: int32(id),
		})

	fmt.Printf("\nID: %d\n", book.GetBookId())
	fmt.Printf("Title: %s\n", book.GetTitle())
	fmt.Printf("Author ID: %d\n", book.GetAuthorId())
	fmt.Printf("Publishing year: %d\n", book.GetPublishingYear())
	fmt.Printf("Publishing company ID: %d\n", book.GetPublishingCompanyId())
}

func watchBooks(client pb.LibraryClient) {
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	stream, err := client.WatchBooks(ctx, &pb.WatchBooksRequest{})
	if err != nil {
		log.Println("Failed to start stream:", err)
		return
	}

	fmt.Println("Watching for new books...")

	for {
		book, err := stream.Recv()

		if err == io.EOF {
			fmt.Println("Server closed the stream")
			return
		}

		if err != nil {
			log.Println("Stream error:", err)
			return
		}

		fmt.Printf(
			"New book: %d - %s (%d)\n",
			book.GetBookId(),
			book.GetTitle(),
			book.GetPublishingYear(),
		)
	}
}
