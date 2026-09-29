import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { StudentService } from '../service/student.service';
import { StudentListComponent } from './student-list.component';

describe('StudentListComponent', () => {
  let component: StudentListComponent;
  let fixture: ComponentFixture<StudentListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StudentListComponent],
      imports: [HttpClientTestingModule],
      providers: [StudentService]
    });

    fixture = TestBed.createComponent(StudentListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
