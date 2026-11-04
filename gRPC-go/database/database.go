package database

import (
	"context"
	"database/sql"

	_ "github.com/jackc/pgx/v5/stdlib"
)

type DB struct {
	conn *sql.DB
}

func Open(dsn string) (*DB, error) {
	conn, err := sql.Open("pgx", dsn)
	if err != nil {
		return nil, err
	}

	return &DB{conn: conn}, nil
}

func (db *DB) Close() error {
	return db.conn.Close()
}

func (db *DB) GetBookById(ctx context.Context, id int32) (Book, error) {
	var book Book

	err := db.conn.QueryRowContext(ctx, `
        SELECT
            nBookID,
            cTitle,
            nAuthorID,
            nPublishingYear,
            nPublishingCompanyID
        FROM tbook
        WHERE nBookID = $1
    `, id).Scan(
		&book.ID,
		&book.Title,
		&book.AuthorID,
		&book.PublishingYear,
		&book.PublishingCompanyID,
	)

	return book, err
}

func (db *DB) GetBooks(ctx context.Context) (*sql.Rows, error) {
	return db.conn.QueryContext(ctx, `
        SELECT
            nBookID,
            cTitle,
            nAuthorID,
            nPublishingYear,
            nPublishingCompanyID
        FROM tbook
    `)
}

func (db *DB) CreateBook(
	ctx context.Context,
	title string,
	authorId int32,
	publishingCompanyId int32,
	publishingYear int32,
) (int64, error) {
	var id int64

	err := db.conn.QueryRowContext(ctx, `
        INSERT INTO tbook (
            cTitle,
            nAuthorID,
            nPublishingYear,
            nPublishingCompanyID
        )
        VALUES ($1, $2, $3, $4)
        RETURNING nBookID
    `,
		title,
		authorId,
		publishingYear,
		publishingCompanyId,
	).Scan(&id)

	return id, err
}

// Book, Author, PublishingCompany structs unchanged

type Book struct {
	ID                  int32
	Title               string
	AuthorID            int32
	PublishingYear      int32
	PublishingCompanyID int32
}

type Author struct {
	ID      int32
	Name    string
	Surname string
}

type PublishingCompany struct {
	ID   int32
	Name string
}
