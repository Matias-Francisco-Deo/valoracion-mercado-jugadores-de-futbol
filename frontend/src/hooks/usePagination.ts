import { useState } from "react";

interface UsePaginationOptions<T> {
    items: T[];
    itemsPerPage?: number;
}

export function usePagination<T>({items,itemsPerPage = 10,}: UsePaginationOptions<T>) {
    const [currentPage, setCurrentPage] = useState(0);

    const totalPages = Math.ceil(items.length / itemsPerPage);

    // Evita índices inválidos si la lista se reduce
    const safeCurrentPage = Math.min(currentPage,Math.max(0, totalPages - 1));

    const startIndex = safeCurrentPage * itemsPerPage;

    const paginatedItems = items.slice(startIndex,startIndex + itemsPerPage);

    const goToPage = (page: number) => {
        const nextPage = Math.max( 0, Math.min(page, Math.max(0, totalPages - 1)));

        setCurrentPage(nextPage);
    };

    const nextPage = () => goToPage(safeCurrentPage + 1);

    const previousPage = () => goToPage(safeCurrentPage - 1);

    return {
        currentPage: safeCurrentPage,
        totalPages,
        paginatedItems,
        goToPage,
        nextPage,
        previousPage,
    };
}