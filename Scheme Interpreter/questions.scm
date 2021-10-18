(define (caar x) (car (car x)))
(define (cadr x) (car (cdr x)))
(define (cdar x) (cdr (car x)))
(define (cddr x) (cdr (cdr x)))

; Some utility functions that you may find useful to implement

(define (zip pairs)
  (if (null? (car pairs)) ()
    (cons (map car pairs) (zip (map cdr pairs)))
    )

)


;; Problem 15
;; Returns a list of two-element lists
(define (enumerate s)
  ; BEGIN PROBLEM 15
  (define (index lst-so-far s i)
    (if (null? s) lst-so-far
      (index (append lst-so-far (cons (cons i (cons (car s) ())) ())) (cdr s) (+ i 1))
    )
  )
  (index () s 0)
)
  ; END PROBLEM 15

;; Problem 16

;; Merge two lists LIST1 and LIST2 according to COMP and return
;; the merged lists.
(define (merge comp list1 list2)
  ; BEGIN PROBLEM 16 
  (if (and (null? list1) (null? list2)) ()
    (if (null? list1) list2
      (if (null? list2) list1
        (if (comp (car list1) (car list2))
            (cons (car list1) (cons (car list2) (merge comp (cdr list1) (cdr list2))))
            (cons (car list2) (cons (car list1) (merge comp (cdr list1) (cdr list2))))
        )
      )
    )
  )
)
  ; END PROBLEM 16


;; Problem 17

;; Returns a function that checks if an expression is the special form FORM
(define (check-special form)
  (lambda (expr) (equal? form (car expr))))

(define lambda? (check-special 'lambda))
(define define? (check-special 'define))
(define quoted? (check-special 'quote))
(define let?    (check-special 'let))

(define (convert-to-lambda params body input)
  (append (list (cons 'lambda (cons params (cons body nil)))) input)
)

;; Converts all let special forms in EXPR into equivalent forms using lambda
(define (let-to-lambda expr)
  (cond ((atom? expr)
         ; BEGIN PROBLEM 17
          expr
         ; END PROBLEM 17
         )
        ((quoted? expr)
         ; BEGIN PROBLEM 17
         expr
         ; END PROBLEM 17
         )
        ((or (lambda? expr)
             (define? expr))
         (let ((form   (car expr))
               (params (cadr expr))
               (body   (cddr expr)))
           ; BEGIN PROBLEM 17
           (cons form (cons params (map let-to-lambda body)))
           ; END PROBLEM 17
           ))
        ((let? expr)
         (let ((values (cadr expr))
               (body   (cddr expr)))
           ; BEGIN PROBLEM 17
           (convert-to-lambda (car (zip values)) (car (map let-to-lambda body)) (map let-to-lambda (cadr (zip values))))
           ; END PROBLEM 17
           ))
        (else
         ; BEGIN PROBLEM 17
         (map let-to-lambda expr)
         ; END PROBLEM 17
         )))

