type PageItem = number | "...";

export function getDesktopPages(
    currentPage: number,
    totalPages: number
): PageItem[] {
    // Si entran todas las páginas, las mostramos todas
    if (totalPages <= 7) {
        return Array.from({ length: totalPages }, (_, index) => index);
    }

    const lastPage = totalPages - 1;
    const pages: PageItem[] = [0];

    const start = Math.max(1, currentPage - 2);
    const end = Math.min(lastPage - 1, currentPage + 2);

    // Hay páginas ocultas entre la primera y el bloque actual
    if (start > 1) {
        pages.push("...");
    }

    for (let page = start; page <= end; page++) {
        pages.push(page);
    }

    // Hay páginas ocultas entre el bloque actual y la última
    if (end < lastPage - 1) {
        pages.push("...");
    }

    pages.push(lastPage);

    return pages;
}

export function getMobilePages(
    currentPage: number,
    totalPages: number
): number[] {
    const pages: number[] = [];

    if (currentPage > 0) {
        pages.push(currentPage - 1);
    }

    pages.push(currentPage);

    if (currentPage < totalPages - 1) {
        pages.push(currentPage + 1);
    }

    return pages;
}