package database

import (
	"context"
	"database/sql"

	_ "modernc.org/sqlite"
)

type DB struct {
	conn *sql.DB
}

func Open(path string) (*DB, error) {
	conn, err := sql.Open("sqlite", "file:"+path+"?mode=rw")

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
        WHERE nBookID = ?
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
	result, err := db.conn.ExecContext(ctx, `
        INSERT INTO tbook (
            cTitle,
            nAuthorID,
            nPublishingYear,
            nPublishingCompanyID
        )
        VALUES (?, ?, ?, ?)
    `,
		title,
		authorId,
		publishingYear,
		publishingCompanyId,
	)

	if err != nil {
		return 0, err
	}

	return result.LastInsertId()
}

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
